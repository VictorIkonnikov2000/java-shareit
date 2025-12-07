package ru.practicum.shareit.server.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    // Eager загрузка items И requestor для запросов пользователя
    @EntityGraph(attributePaths = {"items", "requestor"})
    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requestorId);

    // Eager загрузка items И requestor для запросов других пользователей
    @EntityGraph(attributePaths = {"items", "requestor"})
    Page<ItemRequest> findByRequestorNot(User requestor, Pageable pageable);

    // Eager загрузка items И requestor для получения конкретного запроса по ID
    // Переопределяем стандартный findById, чтобы включить EntityGraph
    @Override // Важно использовать @Override при переопределении
    @EntityGraph(attributePaths = {"items", "requestor"})
    Optional<ItemRequest> findById(Long id);

    // Добавим метод для поиска всех запросов с обоими графами, если потребуется
    @EntityGraph(attributePaths = {"items", "requestor"})
    List<ItemRequest> findAll(); // Переопределяем findAll для полной загрузки
}
