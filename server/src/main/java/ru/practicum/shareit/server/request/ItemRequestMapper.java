package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.ItemMapper; // <--- !!!Импортируем ItemMapper!!!
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final UserMapper userMapper;
    private final ItemMapper itemMapper; // <--- !!!Инжектируем ItemMapper!!!

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());

        if (itemRequest.getRequestor() != null) {
            // Используем toUserDto из userMapper
            dto.setRequestor(userMapper.toUserDto(itemRequest.getRequestor()));
        }

        // <--- !!!ВОТ ГЛАВНОЕ ИЗМЕНЕНИЕ!!!
        // Используем инжектированный itemMapper для маппинга списка Item
        if (itemRequest.getItems() != null && !itemRequest.getItems().isEmpty()) {
            dto.setItems(itemMapper.toItemDtoList(itemRequest.getItems()));
        } else {
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(itemRequestDto.getCreated());
        // Важно: requestor устанавливается в сервисе, а не здесь,
        // так как в itemRequestDto.requestor содержится UserDto, а не User сущность.
        return itemRequest;
    }

    public List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }


}

