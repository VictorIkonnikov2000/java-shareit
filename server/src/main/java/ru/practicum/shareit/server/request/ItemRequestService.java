package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exceptions.NotFoundException;
import ru.practicum.shareit.server.exceptions.ValidationException;
import ru.practicum.shareit.server.item.ItemRepository;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.item.ItemMapper; // Статический импорт ItemMapper
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.UserRepository;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestDto createRequest(ItemRequestDto requestDto, Long requestorId) {
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            throw new ValidationException("Описание запроса не может быть пустым");
        }

        // Используем статический маппер для создания ItemRequest из ItemRequestDto и User
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto, requestor);
        // Если mapper не устанавливает created, можно установить здесь:
        if (request.getCreated() == null) {
            request.setCreated(LocalDateTime.now());
        }

        ItemRequest savedRequest = itemRequestRepository.save(request);
        return ItemRequestMapper.toItemRequestDto(savedRequest);
    }


    public List<ItemRequestDto> getUserRequests(Long requestorId) {
        userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<ItemRequest> requests = itemRequestRepository.findByRequestorId(
                requestorId, Sort.by(Sort.Direction.DESC, "created"));

        return requests.stream()
                .map(request -> mapItemRequestToDtoWithItems(request)) // Используем перенесенный приватный метод
                .collect(Collectors.toList());
    }


    public List<ItemRequestDto> getAllRequests(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (from < 0) {
            throw new ValidationException("Параметр 'from' не может быть отрицательным");
        }
        if (size <= 0) {
            throw new ValidationException("Параметр 'size' должен быть положительным");
        }

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdNot(
                userId, Sort.by(Sort.Direction.DESC, "created"));

        // Логика пагинации переносится сюда:
        // Важно: skip/limit работают с уже загруженными из БД данными.
        // Для эффективной пагинации следует использовать Pageable в репозитории.
        // Здесь предполагается, что список requests может быть большим.
        // Если запросов очень много, то лучше использовать PageRequest в findAll
        // For example: itemRequestRepository.findAll(PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")));
        List<ItemRequest> paginatedRequests = requests.stream()
                .skip(from)
                .limit(size)
                .toList();

        return paginatedRequests.stream()
                .map(request -> mapItemRequestToDtoWithItems(request)) // Используем перенесенный приватный метод
                .collect(Collectors.toList());
    }


    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        // Проверка существования пользователя остается в сервисе, т.к. это бизнес-логика.
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с ID " + requestId + " не найден"));

        return mapItemRequestToDtoWithItems(request);
    }

    // Приватный хелпер-метод для маппинга ItemRequest в ItemRequestDto с прикрепленными предметами.
    private ItemRequestDto mapItemRequestToDtoWithItems(ItemRequest request) {
        List<Item> items = itemRepository.findByRequestId(request.getId());
        List<ItemDto> itemDtos = ItemMapper.toItemDtoList(items); // Используем ItemMapper для списка Item

        return ItemRequestMapper.toItemRequestDtoWithItems(request, itemDtos); // Используем ItemRequestMapper
    }
}
