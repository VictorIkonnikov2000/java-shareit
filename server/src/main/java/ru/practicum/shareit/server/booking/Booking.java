package ru.practicum.shareit.server.booking;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.server.item.Item;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id != null && Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : super.hashCode(); // Или просто 0, или константа
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", status=" + status +
                ", itemId=" + (item != null ? item.getId() : "null") + // Используем ID Item
                ", bookerId=" + (booker != null ? booker.getId() : "null") + // Используем ID Booker
                '}';
    }
}