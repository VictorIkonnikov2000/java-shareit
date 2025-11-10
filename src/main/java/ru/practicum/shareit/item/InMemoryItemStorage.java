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
        // Преобразуем DTO в сущность Item, используя вспомогательный метод
        Item item = fromItemDto(itemDto);
        item.setId(nextId++); // Генерируем уникальный ID
        item.setOwnerId(userId); // Устанавливаем ID владельца
        items.put(item.getId(), item); // Сохраняем в хранилище
        return ItemMapper.toItemDto(item); // Возвращаем DTO
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = items.get(itemId);  // Получаем существующую вещь
        if (existingItem == null || !existingItem.getOwnerId().equals(userId)) {
            return null; // Или выбрасываем исключение, если вещь не найдена или не принадлежит пользователю
        }

        // Обновляем поля вещи, если они указаны в itemDto
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        items.put(itemId, existingItem); // Обновляем вещь в хранилище
        return ItemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = items.get(itemId);  // Получаем вещь по ID
        return (item != null) ? ItemMapper.toItemDto(item) : null;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return items.values().stream()  // Получаем все значения из Map (все вещи)
                .filter(item -> item.getOwnerId().equals(userId)) // Фильтруем вещи по ID владельца
                .map(ItemMapper::toItemDto) // Преобразуем каждую вещь в DTO
                .collect(Collectors.toList()); // Собираем в список
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if(text == null || text.isEmpty()) { // Если текст пустой, возвращаем пустой список
            return List.of();
        }

        String searchText = text.toLowerCase();
        return items.values().stream()  // Получаем все значения из Map
                .filter(Item::getAvailable)  // Фильтруем только доступные вещи
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText)) // Ищем текст в имени и описании
                .map(ItemMapper::toItemDto) // Преобразуем в DTO
                .collect(Collectors.toList()); // Собираем в список
    }

    // Вспомогательный метод для преобразования ItemDto в Item
    private Item fromItemDto(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        return item;
    }
}
