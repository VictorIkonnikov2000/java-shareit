package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
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
        if (itemDto.getAvailable() == null) {
            throw new IllegalArgumentException("Available cannot be null");
        }
        Item item = fromItemDto(itemDto);
        item.setId(nextId++);
        item.setOwnerId(userId);
        items.put(item.getId(), item);
        ItemDto resultDto = ItemMapper.toItemDto(item);
        resultDto.setId(item.getId());
        return resultDto;
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = items.get(itemId);
        if (existingItem == null || !existingItem.getOwnerId().equals(userId)) {
            return null; // или выбрасываем исключение
        }

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
        ItemDto resultDto = ItemMapper.toItemDto(existingItem);
        resultDto.setId(existingItem.getId()); // Устанавливаем ID в DTO
        return resultDto;
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = items.get(itemId);
        return (item != null) ? ItemMapper.toItemDto(item) : null;
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
        if (text == null || text.isEmpty()) {
            return List.of();
        }

        String searchText = text.toLowerCase();
        try {
            return items.values().stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                            item.getDescription().toLowerCase().contains(searchText))
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Item fromItemDto(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}

