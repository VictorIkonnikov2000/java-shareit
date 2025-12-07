package ru.practicum.shareit.server.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.booking.dto.BookingShortDto;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.request.ItemRequest; // Импорт правильный

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName() != null ? item.getName() : "Unknown Item Name");
        itemDto.setDescription(item.getDescription() != null ? item.getDescription() : "No description provided");
        itemDto.setAvailable(item.getAvailable());
        itemDto.setUserId(item.getOwnerId()); // Предполагаем, что userId в DTO - это ownerId
        // Здесь используется item.getRequest() для установки id запроса, это правильно.
        // Но в ItemDto поле названо request, а не requestId.
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId()); // <-- *Исправление №1: Используем requestId в DTO*
        }
        return itemDto;
    }

    public ItemDto toItemDtoWithBookings(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking) {
        ItemDto itemDto = toItemDto(item); // Это вызовет обновленный toItemDto
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        return itemDto;
    }

    public ItemDto toItemDtoWithBookingsAndComments(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking, List<CommentDto> comments) {
        ItemDto itemDto = toItemDto(item); // Это вызовет обновленный toItemDto
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(comments != null ? comments : new ArrayList<>());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, Long userId) { // Используется для создания Item без запроса
        Item item = new Item();
        item.setId(itemDto.getId());
        // Проверки на null можно убрать, если @NotBlank в DTO гарантирует, что name и description не будут null.
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(userId);
        // Здесь не устанавливается request, так как этот метод предназначен для Item без Request
        return item;
    }

    public static Item toItem(ItemDto itemDto) { // Используется для обновления Item
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(itemDto.getUserId()); // Использует userId из DTO. Убедитесь, что это ownerId
        // Здесь также не устанавливается request напрямую, этим должен заниматься сервис.
        return item;
    }

    public Item toItem(ItemDto itemDto, Long userId, ItemRequest itemRequest) {
        Item item = toItem(itemDto, userId); // Правильно вызывает toItem, который не ставит request
        item.setRequest(itemRequest); // <-- *Исправление №2: Здесь request устанавливается для Item*
        return item;
    }
}



