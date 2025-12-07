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
        itemDto.setOwnerId(item.getOwnerId()); // <-- Правильно: ownerId из Item маппится в ownerId в ItemDto

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
        // item.setId(itemDto.getId()); // ID не устанавливаем для нового объекта, он генерируется БД
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(userId); // <-- Правильно: устанавливаем ownerId из userId, переданного в метод
        // item.request здесь не устанавливается, т.к. это метод для item БЕЗ запроса
        return item;
    }

    public Item toItem(ItemDto itemDto) { // Этот метод в текущей логике лучше не использовать или переработать для обновления
        // Он несет риск неправильной установки ownerId и Request
        Item item = new Item();
        item.setId(itemDto.getId()); // ID должен быть!
        // В случае обновления, лучше передавать существующий Item и обновлять его поля
        // Например, toItem(Item existingItem, ItemDto updates)
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        // item.setOwnerId(itemDto.getUserId()); // <-- Избегайте использования ownerId из DTO для создания/обновления,
        // так как ownerId должен быть подтвержденным ID пользователя,
        // а не полученным из DTO пользователя. Лучше получать его из заголовка X-Sharer-User-Id.
        // item.request здесь не устанавливается, этим должен заниматься сервис.
        return item;
    }

    public Item toItem(ItemDto itemDto, Long userId, ItemRequest itemRequest) {
        Item item = toItem(itemDto, userId); // Вызывает toItem(itemDto, userId), который уже установил ownerId
        item.setRequest(itemRequest); // <-- Правильно: устанавливаем request из полученного ItemRequest
        return item;
    }
}



