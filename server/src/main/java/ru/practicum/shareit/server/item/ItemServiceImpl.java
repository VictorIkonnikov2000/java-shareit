package ru.practicum.shareit.server.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.Booking;
import ru.practicum.shareit.server.booking.BookingRepository;
import ru.practicum.shareit.server.booking.dto.BookingShortDto;
import ru.practicum.shareit.server.exceptions.ForbiddenException;
import ru.practicum.shareit.server.exceptions.InvalidItemDataException;
import ru.practicum.shareit.server.exceptions.NotFoundException;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.request.ItemRequest;
import ru.practicum.shareit.server.request.ItemRequestRepository;
import ru.practicum.shareit.server.user.UserService;

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
        userService.getUser(userId); // Проверяем существование пользователя
        validateItemDto(itemDto); // Ваша внутренняя валидация

        Item item;
        // *Проблема №3: Здесь вы проверяете itemDto.getRequest(), а не itemDto.getRequestId()*
        if (itemDto.getRequestId() != null) { // <-- *Исправление №3: Использовать requestId*
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId()) // <-- *Исправление №4: Использовать requestId*
                    .orElseThrow(() -> new NotFoundException("Запрос с id " + itemDto.getRequestId() + " не найден.")); // <-- *Исправлено*
            item = itemMapper.toItem(itemDto, userId, itemRequest); // Это хороший метод маппера, он создает Item
        } else {
            item = itemMapper.toItem(itemDto, userId); // Это статический метод, который не ставит request.
            // Если itemMapper инжектирован, используйте itemMapper.toItem(itemDto, userId);
        }
        item.setOwnerId(userId); // Этот сеттер здесь излишен, так как toItem(itemDto, userId) уже устанавливает ownerId
        // или вы можете убрать userId из toItem и ставить его здесь, если ownerId в itemDto всегда null
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

        List<Booking> allBookings = bookingRepository.findByItem_IdIn(itemIds);

        Map<Long, List<Booking>> bookingsByItemId = allBookings.stream()
                .collect(groupingBy(booking -> booking.getItem().getId()));

        List<Comment> allComments = commentRepository.findByItemIdIn(itemIds);

        Map<Long, List<CommentDto>> commentsByItemId = allComments.stream()
                .collect(groupingBy(comment -> comment.getItem().getId(),
                        Collectors.mapping(commentMapper::toCommentDto, Collectors.toList())));


        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), List.of());

                    Booking lastBooking = itemBookings.stream()
                            .filter(booking -> booking.getItem().getOwnerId().equals(userId))
                            .filter(booking -> booking.getEnd().isBefore(now))
                            .max(Comparator.comparing(Booking::getEnd))
                            .orElse(null);

                    Booking nextBooking = itemBookings.stream()
                            .filter(booking -> booking.getItem().getOwnerId().equals(userId))
                            .filter(booking -> booking.getStart().isAfter(now))
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null);

                    BookingShortDto lastBookingDto = (lastBooking != null) ? convertBookingToBookingShortDto(lastBooking) : null;
                    BookingShortDto nextBookingDto = (nextBooking != null) ? convertBookingToBookingShortDto(nextBooking) : null;

                    List<CommentDto> comments = commentsByItemId.getOrDefault(item.getId(), List.of());

                    return itemMapper.toItemDtoWithBookingsAndComments(item, lastBookingDto, nextBookingDto, comments);
                })
                .sorted(Comparator.comparing(ItemDto::getId))
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
        // 1. Проверяем, что пользователь существует (эта строка уже вызывает getUser, которая проверит существование)
        userService.getUser(userId); // Фактически нам нужен только ID, поэтому UserDto здесь можно не сохранять, если не используется

        // 2. Проверяем, что предмет существует
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));

        // 3. Проверяем, что пользователь брал эту вещь в аренду и аренда закончилась.
        List<Booking> bookings = bookingRepository.findByItem_IdAndBooker_IdAndEndBefore(itemId, userId, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new InvalidItemDataException("Пользователь с id " + userId + " не может оставить комментарий к предмету с id " + itemId + ", так как не арендовал его или аренда еще не завершилась.");
        }

        // 4. Если все проверки пройдены, создаем комментарий.
        // ИЗМЕНЕНИЕ ЗДЕСЬ: Передаем userId как authorId
        Comment comment = commentMapper.toComment(commentDto, item, userId);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        // 5. Возвращаем CommentDto с присвоенным id
        return commentMapper.toCommentDto(savedComment);
    }
}
