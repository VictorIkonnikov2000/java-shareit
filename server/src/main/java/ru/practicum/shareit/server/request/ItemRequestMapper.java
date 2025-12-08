package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.ItemMapper;
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
    private final ItemMapper itemMapper; // Убедимся, что itemMapper инжектируется

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());

        if (itemRequest.getRequestor() != null) {
            dto.setRequestor(userMapper.toUserDto(itemRequest.getRequestor()));
        } else {
            // Если requestor вдруг null (чего быть не должно, т.к. это обязательное поле)
            dto.setRequestor(null);
        }

        // Используем itemMapper для преобразования списка Item в список ItemDto
        if (itemRequest.getItems() != null && !itemRequest.getItems().isEmpty()) {
            dto.setItems(itemMapper.toItemDtoList(itemRequest.getItems()));
        } else {
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        // ID и created будут установлены базой или сервисом, requestor - сервисом

        // Используем itemMapper для преобразования списка ItemDto в список Item
        if (itemRequestDto.getItems() != null && !itemRequestDto.getItems().isEmpty()) {
            List<Item> items = itemRequestDto.getItems().stream()
                    .map(itemMapper::toItem) // Используем toItem из ItemMapper
                    .collect(Collectors.toList());
            itemRequest.setItems(items);
        } else {
            itemRequest.setItems(Collections.emptyList()); // Гарантируем, что список не null
        }

        return itemRequest;
    }

    public List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }
}


