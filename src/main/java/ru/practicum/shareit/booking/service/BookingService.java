package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.dto.PostBookingDto;

import java.util.Collection;
import java.util.List;

public interface BookingService {

    Booking addBooking(PostBookingDto postBookingDto, int userId);

    Booking approve(int bookingId, boolean approved, int userId);

    Booking getBooking(int bookingId);

    Booking getBookingForUser(int bookingId, int userId);

    List<Booking> findAllBookings(BookingState state, int userId);

    Collection<Booking> getAllBookingForOwner(BookingState state, int ownerId);
}
