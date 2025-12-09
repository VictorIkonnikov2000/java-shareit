package ru.practicum.shareit.gateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRequestCreateDto {
    @NotBlank(message = "Описание запроса не должно быть пустое")
    private String description;
}
