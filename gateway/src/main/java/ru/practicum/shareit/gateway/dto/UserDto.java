package ru.practicum.shareit.gateway.dto;

import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Название не должно быть пустое.")
    private String name;

    @NotBlank(message = "Электронная почта не должна быть пустая.")
    @Email(message = "Электронная почта должна содержать символ @.")
    private String email;
}