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
@NoArgsConstructor // Добавим конструктор без аргументов для JPA
@AllArgsConstructor // И конструктор со всеми аргументами, если нужно для тестов или удобства
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // По умолчанию LAZY, явно указываем для понимания
    @JoinColumn(name = "requestor_id", nullable = false)
    private User requestor;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true) // Добавил orphanRemoval=true, если items должны удаляться с запросом
    private List<Item> items = new ArrayList<>(); // Инициализация очень важна

    // Конструктор без id, т.к. он генерируется БД
    public ItemRequest(String description, User requestor, LocalDateTime created) {
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }
}
