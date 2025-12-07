package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.ItemMapper; // <-- ИМПОРТИРУЕМ ItemMapper
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper; // <-- ВНЕДРЯЕМ ItemMapper

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());

        if (itemRequest.getRequestor() != null) {
            dto.setRequestor(userMapper.toUserDto(itemRequest.getRequestor()));
            dto.setRequestorName(itemRequest.getRequestor().getName());
        } else {
            dto.setRequestor(null);
            dto.setRequestorName(null);
        }

        if (itemRequest.getItems() != null && !itemRequest.getItems().isEmpty()) {
            // ИСПОЛЬЗУЕМ ВНЕДРЕННЫЙ itemMapper для преобразования списка Item в список ItemDto
            dto.setItems(itemRequest.getItems().stream()
                    .map(itemMapper::toItemDto) // <-- Ключевое изменение здесь
                    .collect(Collectors.toList()));
        } else {
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        // createdAt и requestor будут установлены в сервисе
        return itemRequest;
    }

    public List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

}


