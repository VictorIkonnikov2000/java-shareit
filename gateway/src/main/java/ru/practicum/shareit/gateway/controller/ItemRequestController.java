package ru.practicum.shareit.gateway.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.gateway.client.ItemRequestClient;
import ru.practicum.shareit.gateway.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating ItemRequest {} with userId={}", itemRequestDto, userId);
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByUserId(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Get ItemRequests by userId={}", userId);
        return itemRequestClient.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get all ItemRequests with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllItemRequests(userId, from, size);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long requestId) {
        log.info("Get ItemRequest by requestId={} and userId={}", requestId, userId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }
}

