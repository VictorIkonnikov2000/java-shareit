package ru.practicum.shareit.server.request.dto;

import lombok.Data;
import ru.practicum.shareit.server.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private Long requestorId;  // ID пользователя, а не сам объект
    private LocalDateTime created;
    private List<ItemDto> items; // Список ItemDto
}
