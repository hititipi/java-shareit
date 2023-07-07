package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utils.Messages;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.item.ItemController.USER_ID_HEADER;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    ResponseBookingDto add(@Valid @RequestBody PostBookingDto postBookingDto,
                           @RequestHeader(USER_ID_HEADER) int bookerId) {
        log.info(Messages.addBooking());
        Booking booking = bookingService.addBooking(postBookingDto, bookerId);
        return BookingMapper.toResponseBookingDto(booking);
    }

    @PatchMapping("/{bookingId}")
    ResponseBookingDto approve(@PathVariable int bookingId, @RequestParam boolean approved,
                               @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.approveBooking(bookingId, userId, approved));
        Booking booking = bookingService.approve(bookingId, approved, userId);
        return BookingMapper.toResponseBookingDto(booking);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto findById(@PathVariable int bookingId,
                                       @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.findBooking(bookingId, userId));
        Booking booking = bookingService.getBookingForUser(bookingId, userId);
        return BookingMapper.toResponseBookingDto(booking);
    }

    @GetMapping
    public Collection<ResponseBookingDto> findAllBookings(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                          @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.findAllBookings(state, userId));
        List<Booking> bookings = bookingService.findAllBookings(state, userId);
        return BookingMapper.toBookingReferencedDto(bookings);
    }

    @GetMapping("/owner")
    public Collection<ResponseBookingDto> getAllBookingForOwner(@RequestParam(value = "state", defaultValue = "ALL") BookingState state,
                                                                @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info(Messages.findAllBookingsForOwner(ownerId, state));
        Collection<Booking> bookings = bookingService.getAllBookingForOwner(state, ownerId);
        return BookingMapper.toBookingReferencedDto(bookings);
    }

}
