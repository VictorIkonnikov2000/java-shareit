package ru.practicum.shareit.server.request;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id")
    private User requestor;

    @Column(name = "created")
    private LocalDateTime created;

    @Override
    public String toString() {
        return "ItemRequest{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", requestor=" + (requestor != null ? requestor.getId() : "null") +
                ", created=" + created +
                '}';
    }
}