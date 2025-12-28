package ru.practicum.shareit.server.item; // Изменил пакет на dto, если ItemDto там же

import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.ItemRequest;
import ru.practicum.shareit.server.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner, ItemRequest request) {
        if (itemDto == null || owner == null) { // owner не может быть null
            return null;
        }
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(request) // request может быть null
                .build();
    }

    // Добавляем метод для маппинга списка Item в список ItemDto.
    public static List<ItemDto> toItemDtoList(List<Item> items) {
        if (items == null) {
            return null;
        }
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}





