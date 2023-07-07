package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.Sorts.SORT_BY_START;
import static ru.practicum.shareit.utils.Sorts.SORT_BY_START_DESC;
import static ru.practicum.shareit.validation.ValidationErrors.INVALID_USER_ID;
import static ru.practicum.shareit.validation.ValidationErrors.RESOURCE_NOT_FOUND;

@RequiredArgsConstructor
@Service
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Override
    public Item addItem(Item item) {
        userService.getUser(item.getOwner()); // check
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item updateItem) {
        userService.getUser(updateItem.getOwner()); // check
        Item item = getItem(updateItem.getId());
        if (updateItem.getName() != null && !updateItem.getName().isBlank()) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null && !updateItem.getDescription().isBlank()) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }
        return item;
    }

    @Override
    public Item getItem(int itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND));
    }

    @Override
    public ResponseItemDto getItemForUser(int itemId, int userId) {
        Item item = getItem(itemId);
        List<Comment> comments = commentRepository.findByItemId(itemId);
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (item.getOwner() == userId) {
            lastBooking = bookingRepository.findBookingByItemIdAndStartBefore(item.getId(), now, SORT_BY_START_DESC).stream().findFirst().orElse(null);
            nextBooking = bookingRepository.findBookingByItemIdAndStartAfterAndStatus(item.getId(), now, BookingStatus.APPROVED, SORT_BY_START).stream().findFirst().orElse(null);
        }
        return ItemMapper.toResponseItemDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public Collection<ResponseItemDto> getAll(int userId) {
        Collection<Item> items = itemRepository.findAllByOwnerOrderById(userId);
        Map<Item, List<Booking>> bookingsByItem = findApprovedBookingsByItem(items);
        Map<Item, List<Comment>> comments = findComments(items);
        LocalDateTime now = LocalDateTime.now();
        return items.stream()
                .map(item -> getResponseItemDto(item, bookingsByItem.get(item), comments.get(item), now))
                .collect(Collectors.toList());
    }

    private Map<Item, List<Comment>> findComments(Collection<Item> items) {
        return commentRepository.findByItemIn(items).stream().collect(Collectors.groupingBy(Comment::getItem));
    }

    private Map<Item, List<Booking>> findApprovedBookingsByItem(Collection<Item> items) {
        return bookingRepository.findBookingByItemInAndStatus(items, BookingStatus.APPROVED).stream()
                .collect(Collectors.groupingBy(Booking::getItem, Collectors.toList()));
    }

    private ResponseItemDto getResponseItemDto(Item item, List<Booking> bookings, List<Comment> comments, LocalDateTime now) {
        Booking lastBooking = null;
        Booking nextBooking = null;
        if (bookings != null && !bookings.isEmpty()) {
            lastBooking = bookings.stream().sorted(Comparator.comparing(Booking::getStart))
                    .filter(booking -> booking.getStart().isBefore(now))
                    .findFirst().orElse(null);
            nextBooking = bookings.stream().sorted(Comparator.comparing(Booking::getStart))
                    .filter(booking -> booking.getStart().isAfter(now))
                    .findFirst().orElse(null);
        }
        return ItemMapper.toResponseItemDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public Collection<Item> findItemsByText(String text) {
        if (text == null || text.isBlank()) {
            return Collections.EMPTY_LIST;
        }
        return itemRepository.search(text);
    }

    @Override
    public ResponseCommentDto createComment(CommentDto commentDto, int itemId, int userId) {
        Item item = getItem(itemId);
        User author = userService.getUser(userId);
        Collection<Booking> bookings = bookingRepository.findBookingByItemIdAndBookerIdAndStatusAndStartBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings == null || bookings.isEmpty()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, INVALID_USER_ID);
        }
        Comment comment = CommentMapper.toComment(commentDto, item, author, LocalDateTime.now());
        comment = commentRepository.save(comment);
        return CommentMapper.toResponseCommentDto(comment);
    }
}
