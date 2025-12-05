package ru.practicum.shareit.server.request;

import jakarta.persistence.Table;
import lombok.*;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.item.Item;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Transient // поле не хранится в БД
    @ToString.Exclude
    private List<Item> items; // Список вещей, связанных с этим запросом.  Не мапится напрямую в БД

}