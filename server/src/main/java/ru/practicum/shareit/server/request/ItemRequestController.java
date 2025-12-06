package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemRequestDto> createItemRequest(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestBody ItemRequestDto itemRequestDto) {
        return new ResponseEntity<>(itemRequestService.createItemRequest(userId, itemRequestDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getItemRequestsByUserId(
            @RequestHeader(USER_ID_HEADER) Long userId) {
        return new ResponseEntity<>(itemRequestService.getItemRequestsByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllItemRequests(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return new ResponseEntity<>(itemRequestService.getAllItemRequests(userId, from, size), HttpStatus.OK);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getItemRequestById(
            @RequestHeader(USER_ID_HEADER) Long userId,
            @PathVariable Long requestId) {
        return new ResponseEntity<>(itemRequestService.getItemRequestById(userId, requestId), HttpStatus.OK);
    }
}