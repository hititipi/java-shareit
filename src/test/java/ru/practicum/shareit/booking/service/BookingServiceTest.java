package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.exception.UnsupportedStatusException;
import ru.practicum.shareit.validation.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static ru.practicum.shareit.utils.Sorts.SORT_BY_START_DESC;
import static ru.practicum.shareit.validation.ValidationErrors.*;

public class BookingServiceTest {

    private final BookingRepository bookingRepository = mock((BookingRepository.class));
    private final ItemRepository itemRepository  = mock(ItemRepository.class);
    private final UserRepository userRepository  = mock(UserRepository.class);

    private final BookingService bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);


    @Test
    void addBookingTest(){
        // owner = userID
        User owner = new User(1, "user1", "user1@mail.com");
        User booker = new User(2, "user2", "user2@mail.com");
        Item item1 = Item.builder().id(1).name("item_name1").description("item_description1").owner(owner).build();
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        PostBookingDto bookingDto = PostBookingDto.builder().itemId(item1.getId()).build();
        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, owner.getId()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(INVALID_USER_ID, exception.getMessage());

        // not available
        item1.setAvailable(false);
        exception = assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, booker.getId()));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(ITEM_UNAVAILABLE, exception.getMessage());

        // invalid start
        item1.setAvailable(true);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));
        exception = assertThrows(ValidationException.class, () -> bookingService.addBooking(bookingDto, booker.getId()));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(INVALID_TIME, exception.getMessage());

        // normal
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        Booking booking = Booking.builder().id(1).booker(booker).start(bookingDto.getStart()).end(bookingDto.getEnd()).item(item1).build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Booking result = bookingService.addBooking(bookingDto, booker.getId());
        assertNotNull(result);
        assertEquals(result, booking);
        verify(userRepository, times(1)).findById(2);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveTest(){
        User owner = new User(1, "user1", "user1@mail.com");
        User booker = new User(2, "user2", "user2@mail.com");
        Item item1 = Item.builder().id(1).name("item_name1").description("item_description1").owner(owner).build();
        Booking booking = Booking.builder().id(1).item(item1).booker(booker).build();
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        // invalid user Id
        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.approve(1, true, booker.getId()));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(INVALID_USER_ID, exception.getMessage());

        // invalid status
        booking.setStatus(BookingStatus.APPROVED);
        exception = assertThrows(ValidationException.class, () -> bookingService.approve(1, true, owner.getId()));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(BOOKING_ALREADY_APPROVED, exception.getMessage());

        // normal
        booking.setStatus(BookingStatus.WAITING);
        Booking approvedBooking = Booking.builder().id(booking.getId()).booker(booking.getBooker())
                .item(booking.getItem()).status(BookingStatus.APPROVED).build();
        when(bookingRepository.save(any(Booking.class))).thenReturn(approvedBooking);
        Booking result = bookingService.approve(1, true, owner.getId());

        assertNotNull(result);
        assertEquals(result, booking);
        verify(bookingRepository, times(3)).findById(1);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void getBookingForUser(){
        User owner = new User(1, "user1", "user1@mail.com");
        User booker = new User(2, "user2", "user2@mail.com");
        Item item1 = Item.builder().id(1).name("item_name1").description("item_description1").owner(owner).build();
        Booking booking = Booking.builder().id(1).item(item1).booker(booker).build();
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking));

        // invalid userId
        ValidationException exception = assertThrows(ValidationException.class, () -> bookingService.getBookingForUser(1, 3));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(INVALID_USER_ID, exception.getMessage());

        // normal
        Booking result = bookingService.getBookingForUser(1, 1);
        assertNotNull(result);
        assertEquals(result, booking);
        verify(bookingRepository, times(2)).findById(1);
    }

    @Test
    void findAllBookingsTest(){
        User owner = new User(1, "user1", "user1@mail.com");
        User booker = new User(2, "user2", "user2@mail.com");
        Item item1 = Item.builder().id(1).name("item_name1").description("item_description1").owner(owner).build();
        Booking booking = Booking.builder().id(1).item(item1).booker(booker).build();
        when(userRepository.findById(2)).thenReturn(Optional.of(booker));
        Pageable page = PageRequest.of(0, 20, SORT_BY_START_DESC);
        Page<Booking> pageResult = new PageImpl<>(List.of(booking));

        // CURRENT
        when(bookingRepository.findByBookerIdCurrent(eq(booker.getId()), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        List<Booking> result = bookingService.getAllBookings(BookingState.CURRENT, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerIdCurrent(eq(booker.getId()), any(LocalDateTime.class), eq(page));
        // PAST
        when(bookingRepository.findByBookerIdAndEndIsBefore(eq(booker.getId()), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        result = bookingService.getAllBookings(BookingState.PAST, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerIdAndEndIsBefore(eq(booker.getId()), any(LocalDateTime.class), eq(page));
        // FUTURE
        when(bookingRepository.findByBookerIdAndStartIsAfter(eq(booker.getId()), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        result = bookingService.getAllBookings(BookingState.FUTURE, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerIdAndStartIsAfter(eq(booker.getId()), any(LocalDateTime.class), eq(page));
        // WAITING
        when(bookingRepository.findByBookerIdAndStatus(booker.getId(),  BookingStatus.WAITING, page)).thenReturn(pageResult);
        result = bookingService.getAllBookings(BookingState.WAITING, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerIdAndStatus(booker.getId(),  BookingStatus.WAITING, page);
        // REJECTED
        when(bookingRepository.findByBookerIdAndStatus(booker.getId(),  BookingStatus.REJECTED, page)).thenReturn(pageResult);
        result = bookingService.getAllBookings(BookingState.REJECTED, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerIdAndStatus(booker.getId(),  BookingStatus.REJECTED, page);
        // UNSUPPORTED_STATUS
        UnsupportedStatusException exception  = assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllBookings(BookingState.UNSUPPORTED_STATUS, booker.getId(), 0, 20));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(UNSUPPORTED_STATUS, exception.getMessage());
        // ALL
        when(bookingRepository.findByBookerId(booker.getId(),  page)).thenReturn(pageResult);
        result = bookingService.getAllBookings(BookingState.ALL, booker.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findByBookerId(booker.getId(), page);
    }

    @Test
    void findAllBookingsForOwnerTest(){
        User owner = new User(1, "user1", "user1@mail.com");
        User booker = new User(2, "user2", "user2@mail.com");
        Item item1 = Item.builder().id(1).name("item_name1").description("item_description1").owner(owner).build();
        Booking booking = Booking.builder().id(1).item(item1).booker(booker).build();

        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        Pageable page = PageRequest.of(0, 20, SORT_BY_START_DESC);
        Page<Booking> pageResult = new PageImpl<>(List.of(booking));
        LocalDateTime now = LocalDateTime.now();

        // CURRENT
        when(bookingRepository.findBookingsByItemOwnerCurrent(eq(owner), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        Collection<Booking> result = bookingService.getAllBookingForOwner(BookingState.CURRENT, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingsByItemOwnerCurrent(eq(owner), any(LocalDateTime.class), eq(page));
        // PAST
        when(bookingRepository.findBookingByItemOwnerAndEndIsBefore(eq(owner), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        result = bookingService.getAllBookingForOwner(BookingState.PAST, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingByItemOwnerAndEndIsBefore(eq(owner), any(LocalDateTime.class), eq(page));
        // FUTURE
        when(bookingRepository.findBookingByItemOwnerAndStartIsAfter(eq(owner), any(LocalDateTime.class), eq(page))).thenReturn(pageResult);
        result = bookingService.getAllBookingForOwner(BookingState.FUTURE, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingByItemOwnerAndStartIsAfter(eq(owner), any(LocalDateTime.class), eq(page));
        // WAITING
        when(bookingRepository.findBookingByItemOwnerAndStatus(owner,  BookingStatus.WAITING, page)).thenReturn(pageResult);
        result = bookingService.getAllBookingForOwner(BookingState.WAITING, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingByItemOwnerAndStatus(owner,  BookingStatus.WAITING, page);
        // REJECTED
        when(bookingRepository.findBookingByItemOwnerAndStatus(owner,  BookingStatus.REJECTED, page)).thenReturn(pageResult);
        result = bookingService.getAllBookingForOwner(BookingState.REJECTED, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingByItemOwnerAndStatus(owner,  BookingStatus.REJECTED, page);
        // UNSUPPORTED_STATUS
        UnsupportedStatusException exception  = assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAllBookingForOwner(BookingState.UNSUPPORTED_STATUS, owner.getId(), 0, 20));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals(UNSUPPORTED_STATUS, exception.getMessage());
        // ALL
        when(bookingRepository.findBookingByItemOwner(owner, page)).thenReturn(pageResult);
        result = bookingService.getAllBookingForOwner(BookingState.ALL, owner.getId(), 0, 20);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(booking));
        verify(bookingRepository, times(1)).findBookingByItemOwner(owner, page);
    }

}
