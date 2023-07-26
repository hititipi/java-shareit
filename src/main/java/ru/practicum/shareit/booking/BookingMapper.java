package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingReferencedDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {

    public Booking toBooking(PostBookingDto postBookingDto, Item item, User booker) {
        return Booking.builder()
                .start(postBookingDto.getStart())
                .end(postBookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public BookingReferencedDto toBookingReferencedDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return BookingReferencedDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public ResponseBookingDto toResponseBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return ResponseBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public Collection<ResponseBookingDto> toBookingReferencedDto(Collection<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toResponseBookingDto).collect(Collectors.toList());
    }
}
