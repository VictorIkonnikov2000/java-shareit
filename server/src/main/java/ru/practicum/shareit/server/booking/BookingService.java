package ru.practicum.shareit.server.booking;

import ru.practicum.shareit.server.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto, Long bookerId);

    BookingDto approveBooking(Long bookingId, Long ownerId, boolean approved);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getUserBookings(Long userId, State state, String sortBy, String direction);

    List<BookingDto> getOwnerBookings(Long ownerId, State state, String sortBy, String direction);

}