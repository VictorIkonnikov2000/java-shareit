package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.InvalidItemDataException;
import ru.practicum.shareit.exceptions.ItemForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service 
class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService; // Зависимость от UserService

    // Конструктор для внедрения зависимостей
    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        // Проверяем, существует ли пользователь. Если нет, UserService выбросит NotFoundException.
        userService.getUser(userId);

        // Расширенная проверка обязательных полей для создания предмета
        if (itemDto.getName() == null || itemDto.getName().isBlank() ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank() ||
                itemDto.getAvailable() == null) {
            throw new InvalidItemDataException("Название, описание и статус доступности предмета не могут быть пустыми.");
        }

        return itemStorage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        // 1. Проверяем, существует ли пользователь.
        userService.getUser(userId);

        // 2. Получаем предмет из хранилища (как модель, чтобы работать с его ownerId).
        // Если getItemModel возвращает null, значит предмет не найден.
        Item existingItem = itemStorage.getItemModel(itemId);
        if (existingItem == null) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден.");
        }

        // 3. Проверяем, является ли текущий пользователь владельцем предмета.
        if (!existingItem.getOwnerId().equals(userId)) {
            // Если нет, выбрасываем исключение 403 Forbidden.
            throw new ItemForbiddenException("Пользователь с id " + userId + " не является владельцем предмета с id " + itemId + ".");
        }

        // 4. Если все проверки пройдены, передаем предмет на обновление в хранилище.
        // Хранилище обновит только те поля, которые не null в itemDto.
        ItemDto updatedItem = itemStorage.updateItem(userId, itemId, itemDto);

        // Дополнительная проверка, хотя после всех throw выше, это маловероятно.
        if (updatedItem == null) {
            throw new IllegalStateException("Не удалось обновить предмет с id " + itemId + " после проверки всех условий.");
        }
        return updatedItem;
    }

    @Override
    public ItemDto getItem(Long itemId) {
        ItemDto item = itemStorage.getItem(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден.");
        }
        return item;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        // Проверяем существование пользователя.
        userService.getUser(userId);
        return itemStorage.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}





