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

    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY) // mappedBy указывает на поле "request" в Item
    // fetch = FetchType.LAZY - для оптимизации, чтобы не загружать Item сразу,
    // но при этом поле items будет заполняться, когда к нему обратятся
    @ToString.Exclude
    private List<Item> items;

}