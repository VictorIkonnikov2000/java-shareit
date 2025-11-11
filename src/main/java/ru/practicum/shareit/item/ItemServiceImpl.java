package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.exceptions.NotFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.item.model.Item;


import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserService userService, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        try {
            userService.getUser(userId); // Проверка, существует ли пользователь
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e); //Ошибка 404
        }

        if (itemDto.getAvailable() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Available cannot be null"); //Ошибка 400
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be empty"); //Ошибка 400
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description cannot be empty"); //Ошибка 400
        }

        return itemStorage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        ItemDto existingItemDto = getItem(itemId); // Получаем существующую вещь или выбрасываем исключение NotFound
        Item existingItem = itemMapper.toItem(existingItemDto);
        if (!existingItem.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not the owner"); //Ошибка 403
        }
        return itemStorage.updateItem(userId, itemId, itemDto);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        ItemDto itemDto = itemStorage.getItem(itemId);
        if (itemDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"); //Ошибка 404
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemStorage.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of(); //Если текст пустой, возвращаем пустой лист.
        }

        try {
            return itemStorage.searchItems(text);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Search error", e); //Ошибка 500
        }
    }
}
