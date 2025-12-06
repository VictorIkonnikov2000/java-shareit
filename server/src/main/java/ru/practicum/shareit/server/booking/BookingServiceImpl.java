package ru.practicum.shareit.server.booking;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.booking.dto.BookingDto;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.dto.BookingShortDto;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.ItemMapper;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.UserMapper;
import org.springframework.transaction.annotation.Transactional;


import ru.practicum.shareit.server.exceptions.NotFoundException;
import ru.practicum.shareit.server.exceptions.ForbiddenException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper; // <--- ДОБАВЬТЕ ЭТО
    private final ItemMapper itemMapper; // <--- И ЭТО, так как вы его тоже создаете вручную

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingDto bookingDto, Long userId) {

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found with id: " + bookingDto.getItemId())); // Добавил id для ясности
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId)); // Добавил id для ясности

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item with id " + bookingDto.getItemId() + " is not available for booking.");
        }


        if (item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Owner with id " + userId + " cannot book their own item with id " + bookingDto.getItemId() + ".");

        }


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
    @Transactional
    public BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found with id: " + bookingId)); // Используем ваш NotFoundException

        if (!booking.getItem().getOwnerId().equals(userId)) {
            throw new ForbiddenException("User with id " + userId + " is not the owner of item with id " + booking.getItem().getId() + " related to booking " + bookingId + ".");

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
        responseDto.setStatus(booking.getStatus());// Используйте инжектированный itemMapper вместо создания нового
        ItemDto itemDto = itemMapper.toItemDto(booking.getItem());
        responseDto.setItem(itemDto);

// Используйте инжектированный userMapper вместо статического вызова класса
        UserDto bookerDto = userMapper.toUserDto(booking.getBooker());
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