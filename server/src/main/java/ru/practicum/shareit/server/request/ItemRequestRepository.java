package ru.practicum.shareit.server.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph; // Добавили EntityGraph
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.user.User;

import java.util.List;
import java.util.Optional; // Добавили Optional

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @EntityGraph(attributePaths = {"items", "requestor"}) // <--- ИЗМЕНЕНИЕ ЗДЕСЬ!
    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requestorId);

    @EntityGraph(attributePaths = {"items", "requestor"}) // <--- ИЗМЕНЕНИЕ ЗДЕСЬ!
    Page<ItemRequest> findByRequestorNot(User requestor, Pageable pageable);

    @EntityGraph(attributePaths = {"items", "requestor"}) // <--- ИЗМЕНЕНИЕ ЗДЕСЬ!
    Optional<ItemRequest> findById(Long id);
}