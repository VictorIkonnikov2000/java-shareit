package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.ItemMapper; // <--- Импортируем ItemMapper
import ru.practicum.shareit.server.item.dto.ItemDto; // Этот импорт все еще нужен, т.к. ItemRequestDto использует ItemDto
import ru.practicum.shareit.server.item.Item; // Этот импорт теперь не нужен, если ItemMapper полностью обрабатывает Item'ы
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {private final UserMapper userMapper;
    private final ItemMapper itemMapper; // <--- Внедряем ItemMapper!

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());

        if (itemRequest.getRequestor() != null) {
            dto.setRequestor(userMapper.toUserDto(itemRequest.getRequestor()));
        } else {
            dto.setRequestor(null);
        }

        if (itemRequest.getItems() != null && !itemRequest.getItems().isEmpty()) {
            // <--- Используем внедренный itemMapper для преобразования списка Item в ItemDto
            dto.setItems(itemMapper.toItemDtoList(itemRequest.getItems()));
        } else {
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        // В принципе, здесь можно было бы добавить и маппинг requestor, если бы он был
        // в ItemRequestDto при создании запроса, но обычно requestor устанавливается в сервисе.
        return itemRequest;
    }

    public List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

// <--- УДАЛИТЕ ЭТИ МЕТОДЫ ИЗ ItemRequestMapper, они должны быть в ItemMapper
/*
public ItemDto toItemDto(Item item) {
    ItemDto dto = new ItemDto();
    dto.setId(item.getId());
    dto.setName(item.getName());
    dto.setDescription(item.getDescription());
    dto.setAvailable(item.getAvailable());
    dto.setOwnerId(item.getOwnerId());

    if (item.getRequest() != null) {
        dto.setRequestId(item.getRequest().getId());
    } else {
        dto.setRequestId(null);
    }
    return dto;
}

public List<ItemDto> toItemDtoList(List<Item> items) {
    if (items == null) {
        return Collections.emptyList();
    }
    return items.stream()
            .map(this::toItemDto)
            .collect(Collectors.toList());
}
*/

}


