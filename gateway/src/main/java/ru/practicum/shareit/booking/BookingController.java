package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.utils.Messages;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.utils.Constants.DEFAULT_FROM_VALUE;
import static ru.practicum.shareit.utils.Constants.DEFAULT_SIZE_VALUE;
import static ru.practicum.shareit.utils.Constants.USER_ID_HEADER;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    ResponseEntity<Object> add(@Valid @RequestBody PostBookingDto postBookingDto,
                               @RequestHeader(USER_ID_HEADER) int bookerId) {
        log.info(Messages.addBooking());
        //Booking booking = bookingService.addBooking(postBookingDto, bookerId);
        //return BookingMapper.toResponseBookingDto(booking);
        return bookingClient.createBooking(postBookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<Object> approve(@PathVariable int bookingId, @RequestParam boolean approved,
                               @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.approveBooking(bookingId, userId, approved));
        return bookingClient.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@PathVariable int bookingId,
                                      @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.findBooking(bookingId, userId));
        //Booking booking = bookingService.getBookingForUser(bookingId, userId);
        //return BookingMapper.toResponseBookingDto(booking);
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                         @RequestHeader(USER_ID_HEADER) int userId,
                                                         @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                         @PositiveOrZero int from,
                                                         @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                         @Positive int size) {
        log.info(Messages.findAllBookings(state, userId));
        //List<Booking> bookings = bookingService.getAllBookings(state, userId, from, size);
        //return BookingMapper.toBookingReferencedDto(bookings);
        return bookingClient.getAllBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingForOwner(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                                @RequestHeader(USER_ID_HEADER) int ownerId,
                                                                @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                                @PositiveOrZero int from,
                                                                @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                                @Positive int size) {
        log.info(Messages.findAllBookingsForOwner(ownerId, state));
        //Collection<Booking> bookings = bookingService.getAllBookingForOwner(state, ownerId, from, size);
        //return BookingMapper.toBookingReferencedDto(bookings);
        return bookingClient.getAllBookingForOwner(ownerId, state, from, size);
    }

}
