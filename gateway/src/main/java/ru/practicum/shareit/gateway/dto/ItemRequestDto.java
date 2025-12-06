package ru.practicum.shareit.gateway.dto; // <--- Вот этот пакет

import lombok.Data; // Убедитесь, что lombok установлен и работает

// Необходимо импортировать ItemDto и UserDto, если они используются в других местах
// Но если цель - только requestorId, то UserDto не нужен.
// Предположим, что ItemDto все равно потребуется для списка items
import ru.practicum.shareit.gateway.dto.ItemDto; // Убедитесь, что этот DTO существует в gateway.dto
// import ru.practicum.shareit.gateway.dto.UserDto; // <--- Если UserDto не нужен, удаляем его

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

    // Меняем обратно на requestorId
    private Long requestorId; // <--- Снова меняем на Long requestorId

    private LocalDateTime created;
    private List<ItemDto> items; // Список ItemDto (Убедитесь, что ItemDto также определен в gateway.dto)

}


