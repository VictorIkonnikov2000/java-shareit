package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    // private final ItemRepository itemRepository; // Удален, так как Items загружаются через EntityGraph
    private final ItemRequestMapper itemRequestMapper;

    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));// ПРОБЛЕМА: itemRequestDto.getItems() может быть пустым или null на этом этапе
// Если вы планируете, что items должны быть добавлены попозже, то тест ожидает другого.
// Если Items должны быть сразу, то нужно добавить их в ItemRequestDto при создании.

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());

// !!! ДОБАВИТЬ ПРОВЕРКУ ЗДЕСЬ !!!
        if (itemRequest.getItems() == null || itemRequest.getItems().isEmpty()) {
            // Выбрасываем исключение, если ItemRequest создается без Items,
            // предполагая, что это невалидно для системы.
            throw new IllegalArgumentException("ItemRequest must contain at least one item.");
            // Или, если допустимо, но тест требует item, можно добавить placeholder
            // itemRequest.setItems(List.of(new Item("Default Item", "Placeholder for test", true, itemRequest, requestor)));
            // Но лучше, чтобы данные были осмысленными и создавались тестом / клиентом
        }

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(savedRequest);

    }


    public List<ItemRequestDto> getItemRequestsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        // !!! УДАЛЕНА РУЧНАЯ ЗАГРУЗКА ITEMS !!!
        // for (ItemRequest itemRequest : itemRequests) {
        //     List<Item> items = itemRepository.findByRequest(itemRequest.getId());
        //     itemRequest.setItems(items);
        // }
        return itemRequests.stream() // Теперь просто маппим, т.к. items уже загружены
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }


    public List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorNot(requestor, pageable).getContent();
        // !!! УДАЛЕНА РУЧНАЯ ЗАГРУЗКА ITEMS !!!
        // for (ItemRequest itemRequest : itemRequests) {
        //     List<Item> items = itemRepository.findByRequest(itemRequest.getId());
        //     itemRequest.setItems(items);
        // }
        return itemRequests.stream() // Теперь просто маппим, т.к. items уже загружены
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        // Теперь findById сам загрузит items благодаря @EntityGraph в репозитории
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));
        // !!! УДАЛЕНА РУЧНАЯ ЗАГРУЗКА ITEMS !!!
        // List<Item> items = itemRepository.findByRequest(itemRequest.getId());
        // itemRequest.setItems(items);

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }
}