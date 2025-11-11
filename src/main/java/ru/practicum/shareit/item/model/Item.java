package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor; // Импортировать AllArgsConstructor
import lombok.Data;
import lombok.NoArgsConstructor; // Импортировать NoArgsConstructor

@Data
@AllArgsConstructor // Этот конструктор сработает для ItemMapper.toItem
@NoArgsConstructor // Этот конструктор сработает по умолчанию, если не требуются параметры
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long ownerId;
}

