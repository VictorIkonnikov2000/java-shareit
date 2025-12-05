package ru.practicum.shareit.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.BookingClient;
import ru.practicum.shareit.gateway.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto bookingDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating booking {} with userId={}", bookingDto, userId);
        return bookingClient.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable Long bookingId,
                                                 @RequestParam("approved") Boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Approve booking with bookingId={} and userId={}", bookingId, userId);
        return bookingClient.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get booking with bookingId={} and userId={}", bookingId, userId);
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get user bookings with userId={} and state={}", userId, state);
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get owner bookings with userId={} and state={}", userId, state);
        return bookingClient.getOwnerBookings(userId, state, from, size);
    }
}
