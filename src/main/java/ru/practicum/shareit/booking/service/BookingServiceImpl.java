package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.exception.UnsupportedStatusException;
import ru.practicum.shareit.validation.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.utils.Sorts.SORT_BY_START_DESC;
import static ru.practicum.shareit.validation.ValidationErrors.*;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public Booking addBooking(PostBookingDto postBookingDto, int userId) {
        Item item = itemService.getItem(postBookingDto.getItemId());
        if (item.getOwner() == userId) {
            throw new ValidationException(HttpStatus.NOT_FOUND, INVALID_USER_ID);
        }
        if (!item.getAvailable()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, ITEM_UNAVAILABLE);
        }
        if (postBookingDto.getStart().isAfter(postBookingDto.getEnd()) || postBookingDto.getStart().isEqual(postBookingDto.getEnd())) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, INVALID_TIME);
        }
        User user = userService.getUser(userId);
        Booking booking = BookingMapper.toBooking(postBookingDto, item, user);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(int bookingId, boolean approved, int userId) {
        Booking booking = getBooking(bookingId);
        if (booking.getItem().getOwner() != userId) {
            throw new ValidationException(HttpStatus.NOT_FOUND, INVALID_USER_ID);
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, BOOKING_ALREADY_APPROVED);
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(int bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND));
    }

    @Override
    public Booking getBookingForUser(int bookingId, int userId) {
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner() != userId) {
            throw new ValidationException(HttpStatus.NOT_FOUND, INVALID_USER_ID);
        }
        return booking;
    }

    @Override
    public List<Booking> findAllBookings(BookingState state, int userId) {
        userService.getUser(userId); // check
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingRepository.findByBookerIdCurrent(userId, now, SORT_BY_START_DESC);
            case PAST:
                return bookingRepository.findByBookerIdAndEndIsBefore(userId, now, SORT_BY_START_DESC);
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartIsAfter(userId, now, SORT_BY_START_DESC);
            case WAITING:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, SORT_BY_START_DESC);
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, SORT_BY_START_DESC);
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException(HttpStatus.BAD_REQUEST, UNSUPPORTED_STATUS);
            case ALL:
            default:
                return bookingRepository.findByBookerId(userId, SORT_BY_START_DESC);
        }
    }

    @Override
    public Collection<Booking> getAllBookingForOwner(BookingState state, int ownerId) {
        userService.getUser(ownerId); // check
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingRepository.findBookingsByItemOwnerCurrent(ownerId, now, SORT_BY_START_DESC);
            case PAST:
                return bookingRepository.findBookingByItemOwnerAndEndIsBefore(ownerId, now, SORT_BY_START_DESC);
            case FUTURE:
                return bookingRepository.findBookingByItemOwnerAndStartIsAfter(ownerId, now, SORT_BY_START_DESC);
            case WAITING:
                return bookingRepository.findBookingByItemOwnerAndStatus(ownerId, BookingStatus.WAITING, SORT_BY_START_DESC);
            case REJECTED:
                return bookingRepository.findBookingByItemOwnerAndStatus(ownerId, BookingStatus.REJECTED, SORT_BY_START_DESC);
            case UNSUPPORTED_STATUS:
                throw new UnsupportedStatusException(HttpStatus.BAD_REQUEST, UNSUPPORTED_STATUS);
            case ALL:
            default:
                return bookingRepository.findBookingByItemOwner(ownerId, SORT_BY_START_DESC);
        }
    }
}