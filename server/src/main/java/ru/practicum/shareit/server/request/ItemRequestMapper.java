package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final UserMapper userMapper;

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());

        if (itemRequest.getRequestor() != null) {
            dto.setRequestor(userMapper.toUserDto(itemRequest.getRequestor()));
        } else {
            // Если requestor вдруг null (чего быть не должно, т.к. это обязательное поле)
            // или если вы хотите возвращать DTO без requestor в некоторых случаях
            dto.setRequestor(null);
        }

        // Теперь itemRequest.getItems() уже будет инициализирован,
        // благодаря @EntityGraph в репозитории.
        // Проверка на null для коллекции items обычно не нужна,
        // т.к. JPA инициализирует ее пустой коллекцией, если нет связанных элементов.
        // Но для безопасности можно оставить.
        if (itemRequest.getItems() != null && !itemRequest.getItems().isEmpty()) {
            dto.setItems(toItemDtoList(itemRequest.getItems()));
        } else {
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
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
}
