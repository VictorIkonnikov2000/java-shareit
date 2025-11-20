package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.InvalidItemDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private Long nextId = 1L;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(nextId++);
        item.setOwnerId(userId);


        if (item.getAvailable() == null) {
            throw new InvalidItemDataException("Доступность предмета не может быть null.");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new InvalidItemDataException("Имя предмета не может быть пустым.");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new InvalidItemDataException("Описание предмета не может быть пустым.");
        }

        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {

        Item existingItem = items.get(itemId);


        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }


        items.put(itemId, existingItem);
        return ItemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = items.get(itemId);
        return (item != null) ? ItemMapper.toItemDto(item) : null;
    }

    @Override
    public Item getItemModel(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}


