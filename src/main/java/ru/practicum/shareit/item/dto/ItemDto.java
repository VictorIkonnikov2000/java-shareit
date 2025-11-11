package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
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
    private Long userId; // Это поле в DTO может использоваться для передачи ID владельца в некоторых случаях, или для установки
    // Но в целом, для создания и обновления user_id передается в заголовках или URL
}
