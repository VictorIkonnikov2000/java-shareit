package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    @Size(max = 255, message = "Название не может быть длиннее 255 символов")
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 512, message = "Описание не может быть длиннее 512 символов")
    private String description;
    @NotNull(message = "Статус доступности не может быть пустым")
    private Boolean available;
    private Long userId;

    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;

    private Long request;
    private List<CommentDto> comments = new ArrayList<>(); // <-- Вот здесь! Инициализируем пустым списком
}

