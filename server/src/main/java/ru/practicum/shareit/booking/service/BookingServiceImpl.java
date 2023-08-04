package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.ShareitPageRequest;
import ru.practicum.shareit.validation.ValidationErrors;
import ru.practicum.shareit.validation.exception.UnsupportedStatusException;
import ru.practicum.shareit.validation.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.utils.Sorts.SORT_BY_START_DESC;
import static ru.practicum.shareit.validation.ValidationErrors.*;

@RequiredArgsConstructor
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking addBooking(PostBookingDto postBookingDto, int userId) {
        Item item = findItem(postBookingDto.getItemId());
        if (item.getOwner().getId() == userId) {
            throw new ValidationException(HttpStatus.NOT_FOUND, INVALID_USER_ID);
        }
        if (!item.getAvailable()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, ITEM_UNAVAILABLE);
        }
        if (postBookingDto.getStart().isAfter(postBookingDto.getEnd()) || postBookingDto.getStart().isEqual(postBookingDto.getEnd())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, INVALID_TIME);
        }
        User user = findUser(userId);
        Booking booking = BookingMapper.toBooking(postBookingDto, item, user);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(int bookingId, boolean approved, int userId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new ValidationException(HttpStatus.NOT_FOUND, INVALID_USER_ID);
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, BOOKING_ALREADY_APPROVED);
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingById(int bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingForUser(int bookingId, int userId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new ValidationException(HttpStatus.NOT_FOUND, INVALID_USER_ID);
        }
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings(BookingState state, int userId, int from, int size) {
        findUser(userId); // check
        LocalDateTime now = LocalDateTime.now();
        Pageable page = new ShareitPageRequest(from, size, SORT_BY_START_DESC);
        switch (state) {
            case CURRENT:
                return bookingRepository.findByBookerIdCurrent(userId, now, page).toList();
            case PAST:
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, now, page).toList();
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartIsAfter(userId, now, page).toList();
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, page).toList();
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, page).toList();
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException(HttpStatus.BAD_REQUEST, UNSUPPORTED_STATUS);
            case ALL:
            default:
                return bookingRepository.findByBookerId(userId, page).toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Booking> getAllBookingForOwner(BookingState state, int ownerId, int from, int size) {
        User owner = findUser(ownerId);
        LocalDateTime now = LocalDateTime.now();
        Pageable page = new ShareitPageRequest(from, size, SORT_BY_START_DESC);
        switch (state) {
            case CURRENT:
                return bookingRepository.findBookingsByItemOwnerCurrent(owner, now, page).toList();
            case PAST:
                return bookingRepository.findBookingByItemOwnerAndEndIsBefore(owner, now, page).toList();
            case FUTURE:
                return bookingRepository.findBookingByItemOwnerAndStartIsAfter(owner, now, page).toList();
            case WAITING:
                return bookingRepository.findBookingByItemOwnerAndStatus(owner, BookingStatus.WAITING, page).toList();
            case REJECTED:
                return bookingRepository.findBookingByItemOwnerAndStatus(owner, BookingStatus.REJECTED, page).toList();
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException(HttpStatus.BAD_REQUEST, UNSUPPORTED_STATUS);
            case ALL:
            default:
                return bookingRepository.findBookingByItemOwner(owner, page).toList();
        }
    }

    private Item findItem(int itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND));
    }

    private User findUser(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND, ValidationErrors.RESOURCE_NOT_FOUND));
    }

}
