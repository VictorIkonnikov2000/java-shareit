package ru.practicum.shareit.gateway.dto;

import lombok.Data;


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


    private UserDto requestor;

    private LocalDateTime created;
    private List<ItemDto> items; // Список ItemDto

}

