package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.PostItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.TestUtils.bookerWithoutId;
import static ru.practicum.shareit.TestUtils.ownerWithoutId;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationBookingServiceTest {

    private final EntityManager em;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void addBookingTest() {
        User createdOwner = userService.createUser(ownerWithoutId);
        User createdBooker = userService.createUser(bookerWithoutId);
        Item createdItem = itemService.addItem(PostItemDto.builder().name("item1").description("description1")
                .available(true).build(), createdOwner.getId());
        PostBookingDto postBookingDto = PostBookingDto.builder().itemId(createdItem.getId()).start(LocalDateTime.now()
                        .plusDays(1)).end(LocalDateTime.now().plusDays(2)).build();
        Booking booking = bookingService.addBooking(postBookingDto, createdBooker.getId());

        TypedQuery<Booking> query = em.createQuery("Select b from bookings b where b.id = :bookingId", Booking.class);
        Booking gottenBooking = query.setParameter("bookingId", booking.getId()).getSingleResult();

        assertThat(gottenBooking.getId(), equalTo(booking.getId()));
        assertThat(gottenBooking.getBooker(), equalTo(booking.getBooker()));
        assertThat(gottenBooking.getStatus(), equalTo(booking.getStatus()));
        assertThat(gottenBooking.getStart(), equalTo(booking.getStart()));
        assertThat(gottenBooking.getEnd(), equalTo(booking.getEnd()));

        userService.deleteUser(createdOwner.getId());
        userService.deleteUser(createdBooker.getId());
    }

}
