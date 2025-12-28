package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requests")
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    //Создаёт запрос на вещь
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto requestDto,
                                        @RequestHeader(USER_ID_HEADER) Long requestorId) {
        log.info("Received request to create item request. User ID: {}, Description: {}",
                requestorId, requestDto.getDescription());
        return itemRequestService.createRequest(requestDto, requestorId);
    }

    //Получает список всех запросов, созданных определенным пользователем.
    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Long requestorId) {
        log.info("Fetching item requests for user with ID: {}", requestorId);
        return itemRequestService.getUserRequests(requestorId);
    }

    //Получает список всех запросов от других пользователей, исключая запросы текущего пользователя.
    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all item requests from other users. User ID: {}, From: {}, Size: {}",
                userId, from, size);
        return itemRequestService.getAllRequests(userId, from, size);
    }

    //Получает информацию о конкретном запросе по его идентификатору.
    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable Long requestId,
                                         @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Fetching item request with ID: {}. User ID: {}", requestId, userId);
        return itemRequestService.getRequestById(requestId, userId);
    }
}
