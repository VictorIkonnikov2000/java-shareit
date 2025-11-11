package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage) {
        this.itemStorage = itemStorage;
    }

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        return itemStorage.addItem(userId, itemDto);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return itemStorage.updateItem(userId, itemId, itemDto);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return itemStorage.getItem(itemId);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        return itemStorage.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemStorage.searchItems(text);
    }
}
