package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.InvalidItemDataException;
import ru.practicum.shareit.exceptions.ItemForbiddenException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        // Проверяем, существует ли пользователь, добавляющий предмет.
        // Если пользователя нет, выбрасываем NotFoundException.
        try {
            userService.getUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден"); // Будет 404
        }

        // Проверяем обязательные поля предмета (имя, описание, доступность).
        if (itemDto.getName() == null || itemDto.getName().isEmpty() ||
                itemDto.getDescription() == null || itemDto.getDescription().isEmpty() ||
                itemDto.getAvailable() == null) {
            // Будет 400
            throw new InvalidItemDataException("Название, описание и статус доступности предмета не должны быть пустыми");
        }

        return itemStorage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        // Проверяем, существует ли пользователь, который пытается обновить предмет.
        // Если пользователя нет, выбрасываем NotFoundException.
        try {
            userService.getUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден."); // Будет 404
        }

        // Получаем существующий предмет из хранилища.
        ItemDto existingItem = itemStorage.getItem(itemId);
        if (existingItem == null) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден."); // Будет 404
        }

        // Проверяем, является ли текущий пользователь владельцем предмета.
        // Если не является, выбрасываем ItemForbiddenException, которое вернет 403.
        if (!existingItem.getUserId().equals(userId)) {
            // Будет 403
            throw new ItemForbiddenException("Пользователь с id " + userId + " не является владельцем предмета с id " + itemId + " и не может его обновить.");
        }

        // Обновляем предмет через хранилище.
        ItemDto updatedItem = itemStorage.updateItem(userId, itemId, itemDto);

        // Дополнительная проверка на случай, если хранилище могло вернуть null,
        // хотя после предыдущих проверок предмет должен существовать.
        if (updatedItem == null) {
            throw new NotFoundException("При обновлении произошла непредвиденная ошибка: предмет с id " + itemId + " не был найден после проверки."); // Будет 404
        }
        return updatedItem;
    }

    @Override
    public ItemDto getItem(Long itemId) {
        // Получаем предмет по ID из хранилища.
        ItemDto item = itemStorage.getItem(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден."); // Будет 404
        }
        return item;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        // Проверяем существование пользователя.
        try {
            userService.getUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден."); // Будет 404
        }
        return itemStorage.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        // Если строка поиска пустая или состоит только из пробелов, возвращаем пустой список.
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemStorage.searchItems(text);
    }
}






