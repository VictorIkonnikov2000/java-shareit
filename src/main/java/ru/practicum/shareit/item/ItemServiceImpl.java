package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.InvalidItemDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.Booking;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        userService.getUser(userId);
        validateItemDto(itemDto);
        Item item = ItemMapper.toItem(itemDto, userId);
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

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));

        List<CommentDto> comments = commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toCommentDto) // Используем CommentMapper для маппинга
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
        userService.getUser(userId); // Проверим, что пользователь существует
        List<Item> items = itemRepository.findByOwnerId(userId);

        return items.stream()
                .map(item -> {
                    // Получаем последнее бронирование
                    Booking lastBooking = bookingRepository.findFirstByItem_IdAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now()).orElse(null);
                    // Получаем следующее бронирование
                    Booking nextBooking = bookingRepository.findFirstByItem_IdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now()).orElse(null);

                    // Преобразуем в BookingShortDto (если бронирование найдено)
                    BookingShortDto lastBookingDto = (lastBooking != null) ? convertBookingToBookingShortDto(lastBooking) : null;
                    BookingShortDto nextBookingDto = (nextBooking != null) ? convertBookingToBookingShortDto(nextBooking) : null;

                    // Получаем комментарии для каждого предмета
                    List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                            .map(commentMapper::toCommentDto) // Используем CommentMapper
                            .collect(Collectors.toList());

                    // Используем НОВЫЙ метод маппера для объединения всей информации
                    return itemMapper.toItemDtoWithBookingsAndComments(item, lastBookingDto, nextBookingDto, comments);
                })
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






