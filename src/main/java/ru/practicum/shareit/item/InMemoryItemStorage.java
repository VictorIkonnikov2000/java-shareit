package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.InvalidItemDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository // Указываем, что это компонент Spring для работы с данными
class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>(); // Храним объекты Item, а не ItemDto
    private Long nextId = 1L; // Генератор ID

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        // Создаем модель Item из DTO
        Item item = ItemMapper.toItem(itemDto);
        item.setId(nextId++);
        item.setOwnerId(userId); // Устанавливаем владельца предмета

        // Проверяем обязательные поля на предмет null, если это не было сделано при валидации
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
        return ItemMapper.toItemDto(item); // Преобразуем обратно в DTO для возврата
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        // !!! Хранилище не должно проверять ownerId или бросать NotFoundException напрямую.
        // Это задачи сервисного слоя. Хранилище должно либо вернуть null, либо Item,
        // а сервис уже решает, что делать с этим.
        // В данном случае, так как itemStorage.getItem(itemId) уже возвращает null, если не найдено,
        // и сервис уже проверяет ownerId, хранилище может просто обновить предмет.
        Item existingItem = items.get(itemId);

        // Если ItemServiceImpl вызывает getItemModel() и передает сюда уже валидированный Item,
        // то эта проверка здесь может быть уже излишней или изменена.
        // В текущей архитектуре getItemModel будет вызван в сервисе, и NotFoundException бросится там.
        // Поэтому здесь сосредоточимся на обновлении полей существующего предмета.

        // Важно: здесь мы не бросаем исключения. Сервис должен заботиться об этом.
        // Если предмет существует, обновляем его поля, переданные в itemDto
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) { // Булевы значения могут быть false, поэтому просто проверяем на null
            existingItem.setAvailable(itemDto.getAvailable());
        }
        // ownerId не меняется при обновлении

        items.put(itemId, existingItem); // Обновляем в коллекции HashMap (делаем это явно, чтобы убедиться)
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
        if (text == null || text.isBlank()) { // Используем isBlank() для пустых строк и строк с пробелами
            return List.of();
        }

        String searchText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable) // Должен быть в наличии
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}


