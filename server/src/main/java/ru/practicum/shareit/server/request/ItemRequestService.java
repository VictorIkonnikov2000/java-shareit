package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;  // Используем ItemRequestMapper

    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestDto(savedRequest);
    }

    public List<ItemRequestDto> getItemRequestsByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
            itemRequest.setItems(items);
        }
        return itemRequestMapper.toItemRequestDtoList(itemRequests);
    }


    public List<ItemRequestDto> getAllItemRequests(Long userId, Integer from, Integer size) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequestorNot(requestor, pageable).getContent();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
            itemRequest.setItems(items);
        }
        return itemRequestMapper.toItemRequestDtoList(itemRequests);
    }

    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        userRepository.findById(userId) // Просто проверяем, что пользователь существует
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        // --- КЛЮЧЕВОЕ ИЗМЕНЕНИЕ ---
        List<Item> associatedItems = itemRepository.findByRequestId(itemRequest.getId());
        itemRequest.setItems(associatedItems);
        // --------------------------

        return itemRequestMapper.toItemRequestDto(itemRequest);
    }
}
