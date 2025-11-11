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
    private final UserService userService;


    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {

        userService.getUser(userId);

        if (itemDto.getName() == null || itemDto.getName().isBlank() ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank() ||
                itemDto.getAvailable() == null) {
            throw new InvalidItemDataException("Название, описание и статус доступности предмета не могут быть пустыми.");
        }

        return itemStorage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {

        userService.getUser(userId);


        Item existingItem = itemStorage.getItemModel(itemId);
        if (existingItem == null) {
            throw new NotFoundException("Предмет с id " + itemId + " не найден.");
        }


        if (!existingItem.getOwnerId().equals(userId)) {
            throw new ItemForbiddenException("Пользователь с id " + userId + " не является владельцем предмета с id " + itemId + ".");
        }


        ItemDto updatedItem = itemStorage.updateItem(userId, itemId, itemDto);

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

        userService.getUser(userId);
        return itemStorage.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}





