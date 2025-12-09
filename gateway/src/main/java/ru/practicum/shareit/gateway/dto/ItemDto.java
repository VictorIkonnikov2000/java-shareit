package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название не должно быть пустое")
    private String name;

    @NotBlank(message = "Описание не должно быть пустое")
    private String description;

    @NotNull(message = "Статус должен быть указан")
    private Boolean available;

    private Long requestId;
}
