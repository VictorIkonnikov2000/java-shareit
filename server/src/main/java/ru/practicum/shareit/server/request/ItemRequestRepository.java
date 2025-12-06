package ru.practicum.shareit.server.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph; // Добавили EntityGraph
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.user.User;

import java.util.List;
import java.util.Optional; // Добавили Optional

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    // Eager загрузка items для запросов пользователя
    @EntityGraph(attributePaths = "items")
    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requestorId);

    // Eager загрузка items для запросов других пользователей
    @EntityGraph(attributePaths = "items")
    Page<ItemRequest> findByRequestorNot(User requestor, Pageable pageable);

    // Eager загрузка items для получения конкретного запроса по ID
    // Добавили новый метод с EntityGraph
    @EntityGraph(attributePaths = "items")
    Optional<ItemRequest> findById(Long id); // Важно! Переопределили стандартный findById

}
