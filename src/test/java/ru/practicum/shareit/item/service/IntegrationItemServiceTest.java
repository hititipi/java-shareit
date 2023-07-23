package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.function.AsyncServerResponse;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.shareit.TestUtils.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationItemServiceTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;


    @Test
    void getAllTest(){
        User createdOwner = userService.createUser(User.builder().name("user").email("user@mail.com").build());
        User createdBooker = userService.createUser(User.builder().name("user2").email("user2@mail.com").build());
        Item createdItem = itemService.addItem(ItemDto.builder().name("item1").description("description1").available(true).build(), createdOwner.getId());
        PostBookingDto postLastBookingDto = PostBookingDto.builder().itemId(createdItem.getId()).start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(1)).build();
        Booking lastBooking = bookingService.addBooking(postLastBookingDto, createdBooker.getId());
        bookingService.approve(lastBooking.getId(), true, createdOwner.getId());
        PostBookingDto nextBookingDto = PostBookingDto.builder().itemId(createdItem.getId()).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).build();
        Booking nextBooking = bookingService.addBooking(nextBookingDto, createdBooker.getId());
        bookingService.approve(nextBooking.getId(), true, createdOwner.getId());


        System.out.println("  ==================");
        System.out.println(nextBooking);
        System.out.println("  ==================");


        CommentDto commentDto = new CommentDto("comment");
        itemService.createComment(commentDto, createdItem.getId(), createdBooker.getId());

        List<ResponseItemDto> result = itemService.getAll(createdOwner.getId(),0,20);

       // System.out.println(responseItemDto);

      //  System.out.println(responseItemDto.getLastBooking());

        assertNotNull(result);
        assertEquals(result.size(), 1);

        ResponseItemDto responseItemDto = result.get(0);

        assertNotNull(responseItemDto);

        assertEquals(responseItemDto.getId(), createdItem.getId());
        assertEquals(responseItemDto.getName(), createdItem.getName());
        assertEquals(responseItemDto.getAvailable(), createdItem.getAvailable());
        assertEquals(responseItemDto.getDescription(),createdItem.getDescription());
        assertEquals(responseItemDto.getLastBooking(), BookingMapper.toBookingReferencedDto(lastBooking));
        assertEquals(responseItemDto.getNextBooking(), BookingMapper.toBookingReferencedDto(nextBooking));
        assertNotNull(responseItemDto.getComments());
        assertEquals(responseItemDto.getComments().size() ,1);

        userService.deleteUser(createdOwner.getId());
        userService.deleteUser(createdBooker.getId());
    }

}
