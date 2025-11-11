package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.InvalidItemDataException;
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
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        // Проверяем обязательные поля предмета (имя и описание).
        if (itemDto.getName() == null || itemDto.getName().isEmpty() ||
                itemDto.getDescription() == null || itemDto.getDescription().isEmpty() ||
                itemDto.getAvailable() == null) {
            throw new InvalidItemDataException("Название, описание и статус доступности предмета не должны быть пустыми");
        }

        return itemStorage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        // Проверяем, существует ли пользователь, который пытается обновить предмет.
        // Если пользователя нет, то он не может ничего обновить.
        try {
            userService.getUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }

        // Получаем существующий предмет из хранилища.
        // Это необходимо для проверки его существования и владельца.
        ItemDto existingItem = itemStorage.getItem(itemId);
        if (existingItem == null) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден.");
        }

        // Ключевой момент: проверяем, является ли текущий пользователь владельцем предмета.
        // Если ID пользователя, пришедшего в запросе, не совпадает с ID владельца предмета,
        // выбрасываем исключение. Esto приведет к статусу 403 FORBIDDEN.
        // Использование SecurityException уместно для проблем с правами доступа.
        if (!existingItem.getUserId().equals(userId)) {
            throw new SecurityException("Пользователь с id " + userId + " не является владельцем предмета с id " + itemId + " и не может его обновить.");
        }

        // Обновляем предмет через хранилище.
        // Хранилище должно обновлять только те поля, которые не null в itemDto,
        // чтобы поддерживать частичное обновление.
        ItemDto updatedItem = itemStorage.updateItem(userId, itemId, itemDto);

        // Дополнительная проверка на случай, если хранилище могло вернуть null, хотя в этом сценарии маловероятно.
        if (updatedItem == null) {
            throw new NotFoundException("При обновлении произошла непредвиденная ошибка: предмет с id " + itemId + " не был найден после проверки.");
        }
        return updatedItem;
    }

    @Override
    public ItemDto getItem(Long itemId) {
        // Получаем предмет по ID из хранилища.
        ItemDto item = itemStorage.getItem(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден.");
        }
        return item;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        // Проверяем существование пользователя, если это требуется бизнес-логикой
        // для получения списка его предметов.
        try {
            userService.getUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден.");
        }
        return itemStorage.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        // Проверяем, не является ли строка поиска пустой или состоящей только из пробелов.
        // Если да, возвращаем пустой список, так как искать нечего.
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemStorage.searchItems(text);
    }
}




