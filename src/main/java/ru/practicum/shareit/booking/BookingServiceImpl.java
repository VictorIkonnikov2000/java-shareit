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

import jakarta.persistence.EntityNotFoundException;
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
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item is not available");
        }

        // Проверяем, что пользователь не является владельцем предмета
        if (item.getOwnerId().equals(userId)) {
            throw new EntityNotFoundException("Owner can't book own item.");
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
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Проверяем, что пользователь является владельцем предмета
        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new EntityNotFoundException("User is not the owner of the item");
        }

        if (booking.getStatus() != Status.WAITING) {
            throw new IllegalArgumentException("Booking is not in WAITING state");
        }

        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Проверяем, что пользователь является либо книжным, либо владельцем предмета
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwnerId().equals(userId)) {
            throw new EntityNotFoundException("User is not the booker or owner of the item");
        }

        return convertToResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

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
            throw new IllegalArgumentException("Unknown state: " + state); // Некорректный State
        }

        return bookings.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, String state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

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
            throw new IllegalArgumentException("Unknown state: " + state); // Некорректный State
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


