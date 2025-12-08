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

    // Маппинг Item -> ItemDto
    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName() != null ? item.getName() : "Unknown Item Name");
        itemDto.setDescription(item.getDescription() != null ? item.getDescription() : "No description provided");
        itemDto.setAvailable(item.getAvailable());
        itemDto.setUserId(item.getOwnerId());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    // Маппинг Item -> ItemDto с бронированиями
    public ItemDto toItemDtoWithBookings(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking) {
        ItemDto itemDto = toItemDto(item);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        return itemDto;
    }

    // Маппинг Item -> ItemDto с бронированиями и комментариями
    public ItemDto toItemDtoWithBookingsAndComments(Item item, BookingShortDto lastBooking, BookingShortDto nextBooking, List<CommentDto> comments) {
        ItemDto itemDto = toItemDto(item);
        itemDto.setLastBooking(lastBooking);
        itemDto.setNextBooking(nextBooking);
        itemDto.setComments(comments != null ? comments : new ArrayList<>());
        return itemDto;
    }

    // --- МЕТОДЫ ДЛЯ МАППИНГА ItemDto -> Item ---

    // Основной метод для преобразования ItemDto в Item, когда ownerId и Request устанавливаются позже в сервисе
    public Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        // ownerId и Request будут установлены в соответствующем сервисе
        return item;
    }

    // Метод для создания Item с известным ownerId
    public Item toItem(ItemDto itemDto, Long ownerId) {
        Item item = toItem(itemDto); // Используем базовый маппинг
        item.setOwnerId(ownerId);
        return item;
    }

    // Метод для создания Item с известным ownerId и ItemRequest
    public Item toItem(ItemDto itemDto, Long ownerId, ItemRequest itemRequest) {
        Item item = toItem(itemDto, ownerId); // Используем маппинг с ownerId
        item.setRequest(itemRequest);
        return item;
    }

    // Маппинг списка ItemDto -> списка Item
    public List<Item> toItemList(List<ItemDto> itemDtos) {
        return itemDtos.stream()
                .map(this::toItem) // Использует нестатический toItem(ItemDto)
                .collect(Collectors.toList());
    }

    // Маппинг списка Item -> списка ItemDto
    public List<ItemDto> toItemDtoList(List<Item> items) {
        return items.stream()
                .map(this::toItemDto)
                .collect(Collectors.toList());
    }
}




