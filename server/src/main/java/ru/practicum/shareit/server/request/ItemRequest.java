package ru.practicum.shareit.server.request;

import jakarta.persistence.Table;
import lombok.*;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.item.Item;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false, length = 512)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL) // Или PERSIST, MERGE, REFRESH. Убедитесь, что cascade правильный для вашего случая.
    private List<Item> items = new ArrayList<>(); // Инициализация очень важна

}