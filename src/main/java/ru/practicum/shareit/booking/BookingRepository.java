package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("select b from bookings b " +
            "where b.booker.id = :user_id " +
            "and b.start < :time " +
            "and b.end > :time")
    List<Booking> findByBookerIdCurrent(@Param("user_id") int userId, @Param("time") LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(int userId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(int userId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatus(int userId, BookingStatus bookingStatus, Sort sort);

    @Query("select b " +
            "from bookings b " +
            "where b.item.owner = :user " +
            "and b.start < :time " +
            "and b.end > :time")
    List<Booking> findBookingsByItemOwnerCurrent(@Param("user") User booker, @Param("time") LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemOwnerAndEndIsBefore(User booker, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemOwnerAndStartIsAfter(User booker, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemOwnerAndStatus(User booker, BookingStatus status, Sort sort);

    List<Booking> findBookingByItemOwner(User booker, Sort sort);

    List<Booking> findBookingByItemIdAndStartBefore(int itemId, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemIdAndStartAfterAndStatus(int itemId, LocalDateTime now, BookingStatus status, Sort sort);

    List<Booking> findByBookerId(int userId, Sort sort);

    List<Booking> findBookingByItemIdAndBookerIdAndStatusAndStartBefore(int itemId, int bookerId, BookingStatus status, LocalDateTime now);

    Collection<Booking> findBookingByItemInAndStatus(Collection<Item> items, BookingStatus status);

}
