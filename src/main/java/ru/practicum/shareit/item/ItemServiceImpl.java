package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

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
        // Проверка существования пользователя
        try {
            UserDto user = userService.getUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        // Проверка обязательных полей предмета
        if (itemDto.getName() == null || itemDto.getName().isEmpty() || itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new InvalidItemDataException("Название и описание предмета не должны быть пустыми");
        }
        return itemStorage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        // Проверка существования пользователя (владельца)
        try {
            UserDto user = userService.getUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        // Получаем текущий предмет для проверки прав доступа
        ItemDto existingItem = itemStorage.getItem(itemId);
        if (existingItem == null) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }

        // Проверка прав доступа: только владелец может изменить предмет
        if (existingItem.getUserId() == null || !existingItem.getUserId().equals(userId)) {
            throw new AccessDeniedException("У пользователя с id " + userId + " нет прав на обновление предмета с id " + itemId);
        }

        // Обновляем предмет через хранилище
        ItemDto updatedItem = itemStorage.updateItem(userId, itemId, itemDto);
        // Добавлена проверка на null, если хранилище по каким-то причинам все же вернуло null (хотя при текущей логике уже не должно)
        if (updatedItem == null) {
            // Это может произойти, если предмет был удален между getItem и updateItem,
            // или если в хранилище есть своя специфическая логика, которая возвращает null
            throw new NotFoundException("Предмет с id " + itemId + " не найден для обновления.");
        }
        return updatedItem;
    }

    @Override
    public ItemDto getItem(Long itemId) {
        ItemDto item = itemStorage.getItem(itemId);
        if (item == null) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }
        return item;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        // Если нужно проверять наличие пользователя, можно добавить:
        // try {
        //     userService.getUser(userId);
        // } catch (NotFoundException e) {
        //     throw new NotFoundException("Пользователь с id " + userId + " не найден");
        // }
        return itemStorage.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        // Убрал блок try-catch, так как itemStorage.searchItems уже выбрасывает исключение,
        // а ItemSearchException создается только для обертывания
        return itemStorage.searchItems(text);
    }
}



