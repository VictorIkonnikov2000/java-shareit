package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findByItem_IdIn(List<Long> itemIds);

    List<Booking> findByItem_OwnerIdOrderByStartDesc(Long ownerId);

    Booking findFirstByItem_IdAndItem_OwnerIdAndStatusNotOrderByEndDesc(Long itemId, Long ownerId, Status status);

    Booking findFirstByItem_IdAndItem_OwnerIdAndStatusNotAndStartLessThanEqualOrderByEndDesc(Long itemId, Long ownerId, Status status, LocalDateTime end);

    Booking findFirstByItem_IdAndItem_OwnerIdAndStatusNotAndStartGreaterThanEqualOrderByStartAsc(Long itemId, Long ownerId, Status status, LocalDateTime end);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByItem_OwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByItem_OwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItem_OwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findByItem_OwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    Optional<Booking> findFirstByItem_IdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    Optional<Booking> findFirstByItem_IdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    List<Booking> findByItem_IdAndBooker_IdAndEndBefore(Long itemId, Long bookerId, LocalDateTime end);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = ?2 " +
            "and b.start < CURRENT_TIMESTAMP " +
            "order by b.end desc")
    List<Booking> findLastBookings(Long itemId, Status status);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.status = ?2 " +
            "and b.start > CURRENT_TIMESTAMP " +
            "order by b.start asc")
    List<Booking> findNextBookings(Long itemId, Status status);
}
