package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.UserMapper; // Импорт UserMapper
import ru.practicum.shareit.server.user.dto.UserDto; // Импорт UserDto (на всякий случай, если маппер его напрямую создает)
// Допустим, у вас есть ItemMapper, но пока его нет, оставим методы здесь.

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {

    private final UserMapper userMapper; // Инъекция UserMapper

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());

        // Правильно маппим запросившего пользователя
        if (itemRequest.getRequestor() != null) {
            dto.setRequestor(userMapper.toUserDto(itemRequest.getRequestor()));
        } else {
            // Важно! Если requestor может быть null, то и поле в DTO должно быть null.
            // Или если хотите, чтобы был дефолтный UserDto, но лучше null.
            dto.setRequestor(null);
        }

        // Маппинг списка вещей, связанных с запросом
        if (itemRequest.getItems() != null && !itemRequest.getItems().isEmpty()) {
            dto.setItems(toItemDtoList(itemRequest.getItems()));
        } else {
            // Возвращаем пустой список, а не null, как требует тест
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setDescription(itemRequestDto.getDescription());
        // User (requestor) должен быть установлен в сервисе, используя userId из DTO,
        // так как нужен полноценный объект User из БД.
        // При создании запроса requestor заполняется в сервисе (createItemRequest).
        // При обновлении (если бы было) нужно было бы доставать User из БД.
        if (itemRequestDto.getCreated() != null) { // Убедимся, что created не null
            itemRequest.setCreated(itemRequestDto.getCreated());
        } else {
            itemRequest.setCreated(LocalDateTime.now()); // Fallback, если не устанавливается
        }
        return itemRequest;
    }

    public List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::toItemRequestDto)
                .collect(Collectors.toList());
    }

    // Методы для маппинга Item. Лучше вынести в отдельный ItemMapper.
    public ItemDto toItemDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());

        // ВАЖНО: Добавьте request.id в ItemDto, если это ожидается в тестах или для фронтенда
        // Без этого поля может не быть связи запроса и вещи
        if (item.getRequest() != null) {
            dto.setRequestId(item.getRequest().getId());
        } else {
            dto.setRequestId(null); // Явно указываем null, если нет запроса
        }

        // ownerId в Item, это id пользователя, а в ItemDto это userId (владелец вещи)
        dto.setOwnerId(item.getOwnerId()); // Предполагаю, что в ItemDto поле называется ownerId или userId
        // Из ваших DTO видно, что это userId.
        return dto;
    }

    public List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }
}

