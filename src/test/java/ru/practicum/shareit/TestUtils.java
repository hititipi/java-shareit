package ru.practicum.shareit;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class TestUtils {

    public static LocalDateTime now = LocalDateTime.now().plusHours(1);

    public static User owner = User.builder().id(1).name("owner").email("owner@mail.com").build();
    public static User booker = User.builder().id(1).name("booker").email("booker@mail.com").build();
    public static User requestor = User.builder().id(1).name("requestor").email("requestor@mail.com").build();
    public static ItemRequest request = ItemRequest.builder().id(1).requester(requestor).description("description").created(LocalDateTime.now()).build();


    public static Item item = Item.builder().id(1).name("item").description("description").available(true).owner(owner).itemRequest(request).build();

    public static Booking booking = Booking.builder().id(1).item(item).start(now).end(now.plusDays(1)).booker(booker).status(BookingStatus.WAITING).build();

}
