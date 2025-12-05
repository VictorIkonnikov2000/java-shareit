package ru.practicum.shareit.server.request;


import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setRequestorId(itemRequest.getRequestor().getId());
        dto.setCreated(itemRequest.getCreated());
        if (itemRequest.getItems() != null) {
            dto.setItems(itemRequest.getItems().stream()
                    .map(this::toItemDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        // Не устанавливаем requestor здесь, это делается в сервисе, так как нам нужен User object из базы данных
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }

    public List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    public ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setUserId(item.getOwnerId()); // Использовать ownerId из Item, установить в userId в ItemDto
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        return dto;
    }

    public List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }
}