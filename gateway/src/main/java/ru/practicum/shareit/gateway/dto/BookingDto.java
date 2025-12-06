package ru.practicum.shareit.gateway.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingDto {
    @NotNull(message = "itemId не может быть null")
    private Long itemId;

    @NotNull(message = "Дата начала бронирования не может быть null")
    @Future(message = "Дата начала бронирования должна быть в будущем")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может быть null")
    @Future(message = "Дата окончания бронирования должна быть в будущем")
    private LocalDateTime end;
}
