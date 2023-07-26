package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.PostItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.utils.Sorts.SORT_BY_START;
import static ru.practicum.shareit.utils.Sorts.SORT_BY_START_DESC;

public class ItemServiceTest {

    private ItemRepository itemRepository = mock(ItemRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);
    private ItemService itemService = new ItemServiceImpl(itemRepository, bookingRepository, commentRepository, userRepository, itemRequestRepository);

    private static final Pageable page = PageRequest.of(0, 20);


    @Test
    void addItemTest() {
        PostItemDto postItemDto = new PostItemDto(1, "item_name", "item_description", true, 1);
        User user = new User(1, "user_name", "user@mail.com");
        ItemRequest itemRequest = new ItemRequest(1, "description", user, LocalDateTime.now());
        Item item = ItemMapper.toItem(postItemDto);
        item.setOwner(user);
        item.setItemRequest(itemRequest);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item createdItem = itemService.addItem(postItemDto, 1);

        assertNotNull(createdItem);
        assertEquals(createdItem, item);
        verify(userRepository, times(1)).findById(1);
        verify(itemRequestRepository, times(1)).findById(1);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemTest() {
        Item item = Item.builder().id(1).name("item_name").build();
        User owner = new User(1, "user_name", "user@mail.com");
        item.setOwner(owner);
        when(userRepository.findById(1)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        Item updateItem = itemService.updateItem(item, 1);

        assertNotNull(updateItem);
        assertEquals(updateItem, item);
        verify(userRepository, times(1)).findById(1);
        verify(itemRepository, times(1)).findById(1);
    }

    @Test
    void getItemForUser() {
        User owner = new User(1, "user_name", "user@mail.com");
        User commentAuthor = new User(2, "user_name2", "user2@mail.com");
        Item item = Item.builder().id(1).name("item_name").description("item_description").owner(owner).build();
        List<Comment> comments = List.of(new Comment(1, "text", item, commentAuthor, LocalDateTime.now()));
        Booking lastBooking = Booking.builder().id(1).item(item).booker(commentAuthor).build();
        Booking nextBooking = Booking.builder().id(2).item(item).booker(commentAuthor).build();
        when(itemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(1))
                .thenReturn(comments);
        when(bookingRepository.findBookingByItemIdAndStartBefore(eq(1), any(LocalDateTime.class), eq(SORT_BY_START_DESC)))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findBookingByItemIdAndStartAfterAndStatus(eq(1), any(LocalDateTime.class), eq(BookingStatus.APPROVED), eq(SORT_BY_START)))
                .thenReturn(List.of(nextBooking));

        ResponseItemDto gottenItemDto = itemService.getItemForUser(1, 1);

        assertNotNull(gottenItemDto);
        assertEquals(gottenItemDto, ItemMapper.toResponseItemDto(item, lastBooking, nextBooking, comments));
        verify(itemRepository, times(1)).findById(any(Integer.class));
        verify(bookingRepository, times(1)).findBookingByItemIdAndStartBefore(eq(1), any(LocalDateTime.class), eq(SORT_BY_START_DESC));
        verify(bookingRepository, times(1)).findBookingByItemIdAndStartAfterAndStatus(eq(1), any(LocalDateTime.class), eq(BookingStatus.APPROVED), eq(SORT_BY_START));
    }

    @Test
    void getAllTest() {
        User user1 = new User(1, "user_name", "user@mail.com");
        User user2 = new User(2, "user2", "user2@mail.com");
        Item item1 = Item.builder().id(1).name("item_name1").description("item_description1").owner(user2).build();
        Item item2 = Item.builder().id(1).name("item_name2").description("item_description2").owner(user2).build();
        Booking lastBooking1 = Booking.builder().id(1).item(item1).booker(user1).start(LocalDateTime.now().minusDays(1)).build();
        Booking nextBooking1 = Booking.builder().id(2).item(item1).booker(user1).start(LocalDateTime.now().plusDays(1)).build();
        Booking lastBooking2 = Booking.builder().id(3).item(item2).booker(user1).start(LocalDateTime.now().minusDays(1)).build();
        Booking nextBooking2 = Booking.builder().id(4).item(item2).booker(user1).start(LocalDateTime.now().plusDays(1)).build();

        List<Comment> comments = List.of(new Comment(1, "text", item1, user1, LocalDateTime.now()));


        when(userRepository.findById(1)).thenReturn(Optional.of(user1));


        when(itemRepository.findAllByOwnerOrderById(user1, page))
                .thenReturn(new PageImpl<>(List.of(item1, item2)));

        when(bookingRepository.findBookingByItemInAndStatus(any(), eq(BookingStatus.APPROVED)))
                .thenReturn(List.of(lastBooking1, nextBooking1, lastBooking2, nextBooking2));

        when(commentRepository.findByItemIn(any()))
                .thenReturn(comments);

        Collection<ResponseItemDto> result = itemService.getAll(1, 0, 20);

        ResponseItemDto itemDto1 = ResponseItemDto.builder().id(item1.getId()).name(item1.getName()).description(item1.getDescription())
                .lastBooking(BookingMapper.toBookingReferencedDto(lastBooking1))
                .nextBooking(BookingMapper.toBookingReferencedDto(nextBooking1))
                .comments(CommentMapper.toResponseCommentDto(comments))
                .build();
        ResponseItemDto itemDto2 = ResponseItemDto.builder().id(item2.getId()).name(item2.getName()).description(item2.getDescription())
                .lastBooking(BookingMapper.toBookingReferencedDto(lastBooking2))
                .nextBooking(BookingMapper.toBookingReferencedDto(nextBooking2))
                .build();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(itemDto1));
        assertTrue(result.contains(itemDto2));

        verify(userRepository, times(1)).findById(any(Integer.class));
        verify(itemRepository, times(1)).findAllByOwnerOrderById(user1, page);
        verify(bookingRepository, times(1)).findBookingByItemInAndStatus(any(), eq(BookingStatus.APPROVED));
        verify(commentRepository, times(1)).findByItemIn(any());
    }

    @Test
    void findItemsByTextTest() {
        Collection<ResponseItemDto> emptyResult = itemService.findItemsByText("", 0, 20);
        assertTrue(emptyResult.isEmpty());
        User user1 = new User(1, "user_name", "user@mail.com");

        Item item1 = Item.builder().id(1).name("item_name1").description("item_description1").owner(user1).build();
        String text = "текст";

        when(itemRepository.search(text, page))
                .thenReturn(List.of(item1));

        Collection<ResponseItemDto> result = itemService.findItemsByText(text, 0, 20);


        verify(itemRepository, times(1)).search(text, page);
        assertNotNull(result);
        assertEquals(1, result.size());

        ResponseItemDto itemDto1 = ResponseItemDto.builder().id(item1.getId()).name(item1.getName()).description(item1.getDescription())
                .build();
        assertTrue(result.contains(itemDto1));
    }


    @Test
    void createCommentTest() {
        User user1 = new User(1, "user_name", "user@mail.com");
        User user2 = new User(2, "user2", "user2@mail.com");
        Item item1 = Item.builder().id(1).name("item_name1").description("item_description1").owner(user2).build();
        LocalDateTime now = LocalDateTime.now();
        Booking booking = Booking.builder().id(1).item(item1).booker(user1).start(now.minusDays(1)).build();


        Comment comment = Comment.builder().id(1).text("text").author(user1).item(item1).created(now).build();

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(any(Integer.class))).thenReturn(Optional.of(item1));
        when(bookingRepository.findBookingByItemIdAndBookerIdAndStatusAndStartBefore(eq(1), eq(1), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto commentDto = new CommentDto("text");
        ResponseCommentDto result = itemService.createComment(commentDto, 1, 1);

        assertNotNull(result);
        ResponseCommentDto dto = ResponseCommentDto.builder().id(1).text("text").authorName(user1.getName()).created(now).build();
        assertEquals(result, dto);
    }
}
