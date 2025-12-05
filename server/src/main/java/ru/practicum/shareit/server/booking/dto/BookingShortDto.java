package ru.practicum.shareit.server.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingShortDto {
    private Long bookingId;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}