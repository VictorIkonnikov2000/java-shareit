package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserMapper;

// Обязательно импортируем ваш собственный NotFoundException
import ru.practicum.shareit.exceptions.NotFoundException; // <--- ВАЖНО: ваш кастомный NotFoundException

// Убираем jakarta.persistence.EntityNotFoundException, если не используем его больше

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponseDto createBooking(BookingDto bookingDto, Long userId) {
        // Используем ваш NotFoundException для случаев "не найдено"
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + bookingDto.getItemId())); // Добавил id для ясности
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId)); // Добавил id для ясности

        // Проверяем доступность предмета. Это IllegalArgumentException,
        // так как запрос содержит неверные данные (предмет недоступен).
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item with id " + bookingDto.getItemId() + " is not available for booking.");
        }

        // Проверяем, что пользователь не является владельцем предмета.
        // Это также IllegalArgumentException, так как это не "не найден",
        // а нарушение бизнес-правила в запросе.
        if (item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Owner with id " + userId + " cannot book their own item with id " + bookingDto.getItemId() + ".");
            // Примечание: тут было EntityNotFoundException. Если вы хотите, чтобы это было NotFoundException (404), то так и будет.
            // Но обычно, когда владелец пытается забронировать свою вещь, это ближе к 400 Bad Request
            // или какому-то специализированному бизнес-исключению (например, ValidationException/BadRequestException).
            // Если оставить NotFoundException, это будет 404, что может ввести в заблуждение,
            // потому что и предмет, и владелец существуют.
            // Я бы рекомендовал использовать IllegalArgumentException или подобный для этого случая.
            // Если вы твердо хотите 404, оставьте NotFoundException.
        }

        // Добавим проверку на корректность дат
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Booking start date cannot be in the past.");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new IllegalArgumentException("Booking end date cannot be before or equal to start date.");
        }


        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(Status.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return convertToResponseDto(savedBooking);
    }

    @Override
    public BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId)); // Используем ваш NotFoundException

        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("User with id " + userId + " is not the owner of item with id " + booking.getItem().getId() + " related to booking " + bookingId + ".");
            // Опять же, если владелец не соответствует, это может быть 403 Forbidden или 404, если вы хотите скрыть существование бронирования.
            // Если вы хотите явно указать на ошибку доступа, то 403.
            // Я оставил NotFoundException, как было раньше, если вы хотите 404.
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new IllegalArgumentException("Booking with id " + bookingId + " is not in WAITING state and cannot be approved/rejected.");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId)); // Используем ваш NotFoundException

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new NotFoundException("User with id " + userId + " is not authorized to view booking with id " + bookingId + ".");
            // Опять же, для ошибок доступа 403 тоже подходит. Но если вы хотите использовать 404
            // для "скрытия" бронирований, к которым нет доступа, то NotFoundException уместен.
        }

        return convertToResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId)); // Используем ваш NotFoundException

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        try {
            BookingState bookingState = BookingState.valueOf(state.toUpperCase());

            switch (bookingState) {
                case CURRENT:
                    bookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                    break;
                case PAST:
                    bookings = bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(userId, now);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(userId, now);
                    break;
                case WAITING:
                    bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, Status.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                    break;
                default:
                    bookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId);
                    break;
            }
        } catch (IllegalArgumentException e) {
            // "Unknown state": это ошибка в запросе, поэтому IllegalArgumentException корректен
            throw new IllegalArgumentException("Unknown state: " + state + ". Allowed states are ALL, WAITING, APPROVED, REJECTED, CURRENT, PAST, FUTURE.");
        }

        return bookings.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId)); // Используем ваш NotFoundException

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        try {
            BookingState bookingState = BookingState.valueOf(state.toUpperCase());

            switch (bookingState) {
                case CURRENT:
                    bookings = bookingRepository.findByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                    break;
                case PAST:
                    bookings = bookingRepository.findByItem_OwnerIdAndEndBeforeOrderByStartDesc(userId, now);
                    break;
                case FUTURE:
                    bookings = bookingRepository.findByItem_OwnerIdAndStartAfterOrderByStartDesc(userId, now);
                    break;
                case WAITING:
                    bookings = bookingRepository.findByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                    break;
                case REJECTED:
                    bookings = bookingRepository.findByItem_OwnerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                    break;
                default:
                    bookings = bookingRepository.findByItem_OwnerIdOrderByStartDesc(userId);
                    break;
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + state + ". Allowed states are ALL, WAITING, APPROVED, REJECTED, CURRENT, PAST, FUTURE.");
        }

        return bookings.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private BookingResponseDto convertToResponseDto(Booking booking) {
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(booking.getId());
        responseDto.setStart(booking.getStart());
        responseDto.setEnd(booking.getEnd());
        responseDto.setStatus(booking.getStatus());

        // Создаем экземпляр ItemMapper
        ItemMapper itemMapper = new ItemMapper();
        ItemDto itemDto = itemMapper.toItemDto(booking.getItem());
        responseDto.setItem(itemDto);

        UserDto bookerDto = UserMapper.toUserDto(booking.getBooker());
        responseDto.setBooker(bookerDto);

        return responseDto;
    }


    public BookingShortDto convertToBookingShortDto(Booking booking) {
        BookingShortDto bookingShortDto = new BookingShortDto();
        bookingShortDto.setBookingId(booking.getId());
        bookingShortDto.setBookerId(booking.getBooker().getId());
        return bookingShortDto;
    }
}


