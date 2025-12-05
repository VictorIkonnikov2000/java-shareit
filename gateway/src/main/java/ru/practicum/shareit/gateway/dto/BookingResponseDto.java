package ru.practicum.shareit.gateway.dto;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private ItemDto item;
    private UserDto booker;
}