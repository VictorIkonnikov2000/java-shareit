package ru.practicum.shareit.gateway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 255, message = "Имя не может быть длиннее 255 символов")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Email должен быть валидным")
    @Size(max = 512, message = "Email не может быть длиннее 512 символов")
    private String email;
}
