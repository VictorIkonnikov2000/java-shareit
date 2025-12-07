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
        itemDto.setName(item.getName()); // Убрал проверки на null, они должны быть в валидации DTO
        itemDto.setDescription(item.getDescription()); // Убрал проверки на null
        itemDto.setAvailable(item.getAvailable());
        itemDto.setUserId(item.getOwnerId()); // <-- Правильно: ownerId из Item маппится в userId в ItemDto

        // Проверяем наличие запроса и устанавливаем requestId в ItemDto
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        } else {
            // Если Item не привязан к запросу, requestId должен быть null
            itemDto.setRequestId(null);
        }
        return itemDto;
    }

    public ItemDto toItemDtoWithBookings(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking) {
        ItemDto itemDto = toItemDto(item);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        return itemDto;
    }

    public ItemDto toItemDtoWithBookingsAndComments(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking, List<CommentDto> comments) {
        ItemDto itemDto = toItemDto(item);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(comments != null ? comments : new ArrayList<>());
        return itemDto;
    }

    public Item toItem(ItemDto itemDto, Long userId) { // Используется для создания Item без запроса
        Item item = new Item();
        item.setId(itemDto.getId()); // ID может быть установлен только если это обновление,
        // для создания ID будет генерироваться БД
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(userId); // <-- Правильно: устанавливаем ownerId из userId, переданного в метод
        // item.request здесь не устанавливается, т.к. это метод для item БЕЗ запроса
        return item;
    }

    public Item toItem(ItemDto itemDto) { // Используется для обновления Item
        Item item = new Item();
        item.setId(itemDto.getId()); // ID должен быть!
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(itemDto.getUserId()); // <-- Правильно: ownerId из ItemDto.userId
        // item.request здесь не устанавливается, этим должен заниматься сервис.
        return item;
    }

    public Item toItem(ItemDto itemDto, Long userId, ItemRequest itemRequest) {
        Item item = toItem(itemDto, userId); // Вызывает toItem(itemDto, userId)
        item.setRequest(itemRequest); // <-- Правильно: устанавливаем request из полученного ItemRequest
        return item;
    }
}



