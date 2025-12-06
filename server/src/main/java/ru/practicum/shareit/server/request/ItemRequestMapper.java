package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.dto.ItemDto; // Убедитесь, что этот импорт правильный
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
            dto.setRequestor(null);
        }

        // Этот блок теперь будет использовать реализованный ниже метод toItemDtoList
        if (itemRequest.getItems() != null && !itemRequest.getItems().isEmpty()) {
            dto.setItems(toItemDtoList(itemRequest.getItems()));
        } else {
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        // ID обычно генерируется БД, при создании нового запроса его не должно быть в DTO
        // itemRequest.setId(itemRequestDto.getId()); // Закомментировано
        itemRequest.setDescription(itemRequestDto.getDescription());
        // Created устанавливается в сервисе при создании
        // requestor также устанавливается в сервисе
        return itemRequest;
    }

    public List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    // --- Измененные/Добавленные методы для маппинга Item ---

    // Метод для преобразования Item в ItemDto
    public ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName()); // <-- ЭТО БЫЛО КЛЮЧЕВЫМ ДЛЯ Postman-теста
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setOwnerId(item.getOwnerId()); // Устанавливаем ownerId

        // ВАЖНО: Добавьте request.id в ItemDto, если оно не было в вашем ItemDto
        // Это поле обычно называется requestId или request
        if (item.getRequest() != null) {
            // Предполагаем, что в ItemDto есть поле Long requestId
            dto.setRequestId(item.getRequest().getId());
        } else {
            dto.setRequestId(null);
        }
        return dto;
    }

    // Метод для преобразования списка Item в список ItemDto
    public List<ItemDto> toItemDtoList(List<Item> items) {
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(this::toItemDto) // Теперь используем this::toItemDto
                .collect(Collectors.toList());
    }
}


