package ru.practicum.shareit.server.request;

import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    // Для конвертации ItemRequest в ItemRequestDto
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor() != null ? itemRequest.getRequestor().getId() : null)
                .created(itemRequest.getCreated())
                .build();
    }

    // Для конвертации ItemRequest в ItemRequestDto с прикрепленными предметами
    // (Этот метод будет вызывать toItemRequestDto и устанавливать список ItemDto)
    public static ItemRequestDto toItemRequestDtoWithItems(ItemRequest itemRequest, List<ItemDto> items) {
        ItemRequestDto dto = toItemRequestDto(itemRequest);
        if (dto != null) {
            dto.setItems(items);
        }
        return dto;
    }

    // Для конвертации входящего ItemRequestDto и User в сущность ItemRequest для сохранения
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        if (itemRequestDto == null || requestor == null) {
            return null;
        }
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(requestor)
                // created будет устанавливаться в сервисе, если не пришло в DTO
                .created(itemRequestDto.getCreated() != null ? itemRequestDto.getCreated() : LocalDateTime.now())
                .build();
    }
}

