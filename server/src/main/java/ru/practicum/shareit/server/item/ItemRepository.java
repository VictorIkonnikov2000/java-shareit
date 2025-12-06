package ru.practicum.shareit.server.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwnerId(Long ownerId);


    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true " +
            "AND (LOWER(i.name) LIKE LOWER(concat('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(concat('%', :text, '%')))")
    List<Item> searchAvailableItems(String text);

    @Query("SELECT i FROM Item i WHERE i.request.id = :requestId")
    List<Item> findByRequest(@Param("requestId") Long requestId);
}

