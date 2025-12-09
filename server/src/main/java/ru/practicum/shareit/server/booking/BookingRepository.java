package ru.practicum.shareit.server.booking;

import ch.qos.logback.core.status.Status;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b JOIN FETCH b.booker JOIN FETCH b.item WHERE b.id = :id")
    Optional<Booking> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item WHERE b.booker.id = :bookerId")
    List<Booking> findByBookerId(@Param("bookerId") Long bookerId, Sort sort);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByBookerId(@Param("bookerId") Long bookerId,
                                        @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b JOIN FETCH b.booker JOIN FETCH b.item " +
            "WHERE b.item.owner.id = :ownerId AND b.start <= :now AND b.end >= :now")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId,
                                       @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b JOIN FETCH b.booker JOIN FETCH b.item " +
            "WHERE b.item.owner.id = :ownerId AND b.end < :now")
    List<Booking> findByItemOwnerIdAndEndBefore(@Param("ownerId") Long ownerId,
                                                @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b JOIN FETCH b.booker JOIN FETCH b.item " +
            "WHERE b.item.owner.id = :ownerId AND b.start > :now")
    List<Booking> findByItemOwnerIdAndStartAfter(@Param("ownerId") Long ownerId,
                                                 @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b JOIN FETCH b.booker JOIN FETCH b.item " +
            "WHERE b.item.owner.id = :ownerId AND b.status = :status")
    List<Booking> findByItemOwnerIdAndStatus(@Param("ownerId") Long ownerId,
                                             @Param("status") BookingStatus status, Sort sort);

    List<Booking> findByItem_IdIn(List<Long> itemIds);

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

    // Явно определяем JOIN FETCH для findNextBookings и findLastBookings
    @Query("SELECT b FROM Booking b JOIN FETCH b.booker JOIN FETCH b.item " +
            "where b.item.id = :itemId " +
            "and b.status = :status " +
            "and b.start < CURRENT_TIMESTAMP " +
            "order by b.end desc")
    List<Booking> findLastBookings(@Param("itemId") Long itemId, @Param("status") Status status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.booker JOIN FETCH b.item " +
            "where b.item.id = :itemId " +
            "and b.status = :status " +
            "and b.start > CURRENT_TIMESTAMP " +
            "order by b.start asc")
    List<Booking> findNextBookings(@Param("itemId") Long itemId, @Param("status") Status status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item WHERE b.booker.id = :bookerId AND b.end < :now")
    List<Booking> findByBookerIdAndEndBefore(@Param("bookerId") Long bookerId,
                                             @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item WHERE b.booker.id = :bookerId AND b.start > :now")
    List<Booking> findByBookerIdAndStartAfter(@Param("bookerId") Long bookerId,
                                              @Param("now") LocalDateTime now, Sort sort);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item WHERE b.booker.id = :bookerId AND b.status = :status")
    List<Booking> findByBookerIdAndStatus(@Param("bookerId") Long bookerId,
                                          @Param("status") BookingStatus status, Sort sort);

    @Query("SELECT b FROM Booking b JOIN FETCH b.booker JOIN FETCH b.item " +
            "WHERE b.item.owner.id = :ownerId")
    List<Booking> findByItemOwnerId(@Param("ownerId") Long ownerId, Sort sort);
}