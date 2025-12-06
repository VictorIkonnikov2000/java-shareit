package ru.practicum.shareit.server.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.booking.dto.BookingShortDto;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.request.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        // ***ВАЖНОЕ ИЗМЕНЕНИЕ ЗДЕСЬ***
        if (item.getName() != null) {
            itemDto.setName(item.getName());
        } else {
            itemDto.setName("Unknown Item Name"); // Заменяем null на строку по умолчанию
        }
        // ***ВАЖНОЕ ИЗМЕНЕНИЕ ЗДЕСЬ***
        if (item.getDescription() != null) {
            itemDto.setDescription(item.getDescription());
        } else {
            itemDto.setDescription("No description provided"); // Заменяем null на строку по умолчанию
        }
        itemDto.setAvailable(item.getAvailable());
        itemDto.setUserId(item.getOwnerId());
        if (item.getRequest() != null) {
            itemDto.setRequest(item.getRequest().getId());
        }
        return itemDto;
    }

    public List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto toItemDtoWithBookings(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking) {
        // Переиспользуем логику toItemDto, чтобы изменения применились автоматически
        ItemDto itemDto = toItemDto(item);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        return itemDto;
    }

    public ItemDto toItemDtoWithBookingsAndComments(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking, List<CommentDto> comments) {
        // Переиспользуем логику toItemDto, чтобы изменения применились автоматически
        ItemDto itemDto = toItemDto(item);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(comments != null ? comments : new ArrayList<>());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto, Long userId) {
        Item item = new Item();
        item.setId(itemDto.getId());
        // ***Добавляем проверку на null в обратном маппинге, если itemDto.getName() может быть null***
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        } else {
            // Опционально: если name не может быть null в базе, возможно, нужно выбросить исключение
            // или установить какое-то дефолтное "пустое" значение, в зависимости от бизнес-логики.
            // item.setName("");
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        } else {
            // item.setDescription("");
        }
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(userId);
        return item;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        // ***Добавляем проверку на null в обратном маппинге***
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(itemDto.getUserId());
        return item;
    }

    public Item toItem(ItemDto itemDto, Long userId, ItemRequest itemRequest) {
        Item item = toItem(itemDto, userId); // Это вызовет наш модифицированный toItem
        item.setRequest(itemRequest);
        return item;
    }
}

