package ru.practicum.shareit.server.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.server.user.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorIdOrderByCreatedDesc(Long requestorId); // Получить свои запросы

    Page<ItemRequest> findByRequestorNot(User requestor, Pageable pageable); // Получить запросы других пользователей
}