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
        try {
            UserDto user = userService.getUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (itemDto.getName() == null || itemDto.getName().isEmpty() || itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new InvalidItemDataException("Название и описание предмета не должны быть пустыми");
        }
        return itemStorage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        try {
            UserDto user = userService.getUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        ItemDto existingItem = itemStorage.getItem(itemId);
        if (existingItem == null) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден");
        }

        if (existingItem.getUserId() == null || !existingItem.getUserId().equals(userId)) {
            throw new AccessDeniedException("Нет прав на обновление этого предмета");
        }

        return itemStorage.updateItem(userId, itemId, itemDto);
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
        return itemStorage.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        try {
            return itemStorage.searchItems(text);
        } catch (Exception e) {
            throw new ItemSearchException("Ошибка при поиске предметов", e);
        }
    }
}


