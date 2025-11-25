package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto createBooking(BookingDto bookingDto, Long userId);

    BookingResponseDto approveBooking(Long bookingId, Boolean approved, Long userId);

    BookingResponseDto getBooking(Long bookingId, Long userId);

    List<BookingResponseDto> getUserBookings(Long userId, String state);

    List<BookingResponseDto> getOwnerBookings(Long userId, String state);
}
