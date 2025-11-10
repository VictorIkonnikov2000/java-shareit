package ru.practicum.shareit.item.model;

import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;          // Уникальный идентификатор вещи
    private String name;       // Краткое название вещи (например, "Дрель Салют")
    private String description; // Подробное описание вещи
    private Boolean available;   // Статус доступности для аренды (true - доступна, false - нет)
    private Long ownerId;     // ID владельца вещи (ссылка на сущность User)
    private Long requestId;    // ID запроса, если вещь была добавлена в ответ на запрос (может быть null)
}

