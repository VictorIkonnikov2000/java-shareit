package ru.practicum.shareit.server.item;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.exceptions.ForbiddenException;
import ru.practicum.shareit.server.exceptions.InvalidItemDataException;
import ru.practicum.shareit.server.exceptions.NotFoundException;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.ItemRequest;
import ru.practicum.shareit.server.request.ItemRequestRepository;
import ru.practicum.shareit.server.user.UserService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.dto.BookingShortDto;
import ru.practicum.shareit.server.booking.Booking;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        userService.getUser(userId);
        validateItemDto(itemDto);
        Item item;
        if (itemDto.getRequest() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequest())
                    .orElseThrow(() -> new NotFoundException("Запрос с id " + itemDto.getRequest() + " не найден."));
            item = itemMapper.toItem(itemDto, userId, itemRequest);
        } else {
            item = ItemMapper.toItem(itemDto, userId);
        }
        item.setOwnerId(userId);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        userService.getUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));

        if (!item.getOwnerId().equals(userId)) {
            throw new ForbiddenException("Пользователь с id " + userId + " не является владельцем предмета с id " + itemId + ".");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        // Нельзя обновить request через updateItem, это логически неверно.
        // Если нужно обновить request, нужно делать это через отдельный endpoint.

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());

        BookingShortDto lastBookingDto = null;
        BookingShortDto nextBookingDto = null;
        if (item.getOwnerId().equals(userId)) {
            // Для отдельного Item все еще делаем прямые запросы, так как их будет всего 2
            Booking lastBooking = bookingRepository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now()).orElse(null);
            Booking nextBooking = bookingRepository.findFirstByItem_IdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now()).orElse(null);

            lastBookingDto = (lastBooking != null) ? convertBookingToBookingShortDto(lastBooking) : null;
            nextBookingDto = (nextBooking != null) ? convertBookingToBookingShortDto(nextBooking) : null;
        }

        return itemMapper.toItemDtoWithBookingsAndComments(item, lastBookingDto, nextBookingDto, comments);
    }


    public ItemDto getItemWithoutBookingsAndComments(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));
        return itemMapper.toItemDto(item);
    }


    @Override
    public List<ItemDto> getItems(Long userId) {
        userService.getUser(userId);
        List<Item> items = itemRepository.findByOwnerId(userId);

        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now();

        // **Оптимизация бронирований:**
        // Получаем все бронирования для всех найденных предметов в одном запросе
        List<Booking> allBookings = bookingRepository.findByItem_IdIn(itemIds);

        // Группируем бронирования по Item ID для быстрого доступа
        Map<Long, List<Booking>> bookingsByItemId = allBookings.stream()
                .collect(groupingBy(booking -> booking.getItem().getId()));

        // **Оптимизация комментариев:**
        // Получаем все комментарии для всех найденных предметов в одном запросе
        List<Comment> allComments = commentRepository.findByItemIdIn(itemIds);

        // Группируем комментарии по Item ID для быстрого доступа
        Map<Long, List<CommentDto>> commentsByItemId = allComments.stream()
                .collect(groupingBy(comment -> comment.getItem().getId(), // ИЗМЕНЕНИЕ ЗДЕСЬ
                        Collectors.mapping(commentMapper::toCommentDto, Collectors.toList())));


        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), List.of());

                    // Находим последнее бронирование
                    Booking lastBooking = itemBookings.stream()
                            .filter(booking -> booking.getItem().getOwnerId().equals(userId)) // Условие, что бронирование относится к вещам пользователя
                            .filter(booking -> booking.getEnd().isBefore(now))
                            .max(Comparator.comparing(Booking::getEnd))
                            .orElse(null);

                    // Находим следующее бронирование
                    Booking nextBooking = itemBookings.stream()
                            .filter(booking -> booking.getItem().getOwnerId().equals(userId)) // Условие, что бронирование относится к вещам пользователя
                            .filter(booking -> booking.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    // Преобразуем в BookingShortDto
                    BookingShortDto lastBookingDto = (lastBooking != null) ? convertBookingToBookingShortDto(lastBooking) : null;
                    BookingShortDto nextBookingDto = (nextBooking != null) ? convertBookingToBookingShortDto(nextBooking) : null;

                    // Получаем комментарии для текущего предмета
                    List<CommentDto> comments = commentsByItemId.getOrDefault(item.getId(), List.of());

                    // Используем маппер
                    return itemMapper.toItemDtoWithBookingsAndComments(item, lastBookingDto, nextBookingDto, comments);
                })
                .sorted(Comparator.comparing(ItemDto::getId)) // Опционально: сортировка по ID для стабильного порядка
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchAvailableItems(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank() ||
                itemDto.getDescription() == null || itemDto.getDescription().isBlank() ||
                itemDto.getAvailable() == null) {
            throw new InvalidItemDataException("Название, описание и статус не могут быть пустыми.");
        }
    }

    private BookingShortDto convertBookingToBookingShortDto(Booking booking) {
        BookingShortDto bookingShortDto = new BookingShortDto();
        bookingShortDto.setBookingId(booking.getId());
        bookingShortDto.setBookerId(booking.getBooker().getId());
        bookingShortDto.setStart(booking.getStart());
        bookingShortDto.setEnd(booking.getEnd());
        return bookingShortDto;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        // 1. Проверяем, что пользователь существует
        UserDto authorDto = userService.getUser(userId);

        // 2. Проверяем, что предмет существует
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        // 3. Проверяем, что пользователь брал эту вещь в аренду и аренда закончилась.
        List<Booking> bookings = bookingRepository.findByItem_IdAndBooker_IdAndEndBefore(itemId, userId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new IllegalStateException("User has not booked this item or booking is not finished yet.");
        }

        // 4. Создаем объект Comment
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthorId(userId);
        comment.setCreated(LocalDateTime.now());

        // 5. Сохраняем комментарий в БД
        comment = commentRepository.save(comment);

        // 6. Создаем и возвращаем CommentDto
        CommentDto result = new CommentDto();
        result.setId(comment.getId());
        result.setText(comment.getText());
        result.setAuthorName(authorDto.getName()); // Используем имя из UserDto
        result.setCreated(comment.getCreated());
        return result;
    }
}
