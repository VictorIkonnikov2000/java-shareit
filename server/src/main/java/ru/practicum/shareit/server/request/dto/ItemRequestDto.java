package ru.practicum.shareit.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto; // Импортируем UserDto

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Описание запроса не может быть пустым")
    @Size(max = 2048, message = "Описание запроса не может быть больше 2048 символов")
    private String description;

    private LocalDateTime created;
    // Вместо requestorId, добавляем UserDto
    private UserDto requestor; // <--- Меняем на объект UserDto

    private List<ItemDto> items; // Список ItemDto
}