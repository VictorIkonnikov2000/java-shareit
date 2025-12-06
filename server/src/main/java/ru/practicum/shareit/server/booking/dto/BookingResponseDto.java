package ru.practicum.shareit.server.booking.dto;

import lombok.Data;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

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