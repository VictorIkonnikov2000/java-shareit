package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto(); // Используем NoArgsConstructor
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setUserId(item.getOwnerId()); // Устанавливаем ownerId как userId в dto
        // itemDto.comments уже ArrayList благодаря ItemDto
        return itemDto;
    }

    // Новый метод, который принимает информацию о бронировании
    public ItemDto toItemDtoWithBookings(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking) {
        ItemDto itemDto = toItemDto(item); // Используем базовый маппер
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        return itemDto;
    }

    public ItemDto toItemDtoWithBookingsAndComments(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking, List<CommentDto> comments) {
        ItemDto itemDto = toItemDto(item); // Используем базовый маппер
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        // Устанавливаем комментарии, убедившись, что переданный список не null
        itemDto.setComments(comments != null ? comments : new ArrayList<>());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, Long userId) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(userId);
        // Если у вас есть поле request в Item, то нужно его установить
        // item.setRequestId(itemDto.getRequest());
        return item;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(itemDto.getUserId());
        // Если у вас есть поле request в Item, то нужно его установить
        // item.setRequestId(itemDto.getRequest());
        return item;
    }
}



