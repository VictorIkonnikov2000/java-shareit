package ru.practicum.shareit.server.request.dto;

import lombok.Data;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto; // Импортируем UserDto

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private UserDto requestor; // <--- Теперь это объект UserDto
    private LocalDateTime created;
    private List<ItemDto> items;
}

