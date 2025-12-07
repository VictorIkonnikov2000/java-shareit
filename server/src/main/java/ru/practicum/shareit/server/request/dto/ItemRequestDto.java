package ru.practicum.shareit.server.request.dto;


import lombok.Data;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto; // Импортируем UserDto

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Описание запроса не может быть пустым")
    @Size(max = 2048, message = "Описание запроса не может быть больше 2048 символов")
    private String description;

    // Вместо requestorId, добавляем UserDto
    private UserDto requestor; // <--- Меняем на объект UserDto
    private String requestorName;

    private LocalDateTime created;
    private List<ItemDto> items; // Список ItemDto


}