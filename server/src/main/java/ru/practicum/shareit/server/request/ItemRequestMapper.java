package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.UserMapper; // Импорт UserMapper

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor // Для инъекции UserMapper
public class ItemRequestMapper {

    private final UserMapper userMapper; // Инъекция UserMapper

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());

        // Маппинг запросившего пользователя
        // itemRequest.getRequestor() вернулся бы в ItemRequestDto как UserDto.
        // Здесь мы маппим его
        if (itemRequest.getRequestor() != null) {
            dto.setRequestor(userMapper.toUserDto(itemRequest.getRequestor()));
        }

        // Маппинг списка вещей, связанных с запросом
        // Здесь мы используем свой метод toItemDto или toItemDtoList для преобразования
        if (itemRequest.getItems() != null && !itemRequest.getItems().isEmpty()) {
            dto.setItems(toItemDtoList(itemRequest.getItems()));
        } else {
            dto.setItems(Collections.emptyList()); // Важно: всегда возвращать список, а не null
        }

        return dto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        // User (requestor) должен быть установлен в сервисе, используя userId из DTO,
        // так как нужен полноценный объект User из БД.
        itemRequest.setCreated(itemRequestDto.getCreated());
        return itemRequest;
    }

    public List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    // Методы для маппинга Item, которые использовались в ItemRequestMapper
    // Если ItemMapper является отдельным компонентом, тогда эти методы можно убрать
    // и инжектировать ItemMapper сюда.
    public ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        // ownerId в Item, это id пользователя, а в ItemDto это userId (владелец вещи)
        dto.setUserId(item.getOwnerId());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        // Если у Item есть поле requestId, и оно нужно в ItemDto, добавьте его.
        // dto.setRequestId(item.getRequest() != null ? item.getRequest().getId() : null);
        return dto;
    }

    public List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }
}
