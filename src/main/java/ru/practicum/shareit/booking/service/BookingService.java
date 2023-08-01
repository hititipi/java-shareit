package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;
import java.util.List;

public interface BookingService {

    Booking addBooking(PostBookingDto postBookingDto, int userId);

    Booking approve(int bookingId, boolean approved, int userId);

    Booking getBookingById(int bookingId);

    Booking getBookingForUser(int bookingId, int userId);

    List<Booking> getAllBookings(BookingState state, int userId, int from, int size);

    Collection<Booking> getAllBookingForOwner(BookingState state, int ownerId, int from, int size);
}
