package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.utils.Sorts.SORT_BY_START_DESC;

public class ItemRequestServiceTest {

    private final ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private ItemRequestService itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

    @Test
    void createItemRequestTest() {
        User requester = new User(1, "user1", "user1@mail.com");
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        ItemRequest request = ItemRequest.builder().id(1).requester(requester).build();
        when(itemRequestRepository.save(request)).thenReturn(request);
        ItemRequest result = itemRequestService.createItemRequest(request, requester.getId());

        assertNotNull(result);
        assertEquals(result, request);
        verify(userRepository, times(1)).findById(requester.getId());
        verify(itemRequestRepository, times(1)).save(request);
    }

    @Test
    void getForOwnerTest() {
        User requester = new User(1, "user1", "user1@mail.com");
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        ItemRequest request = ItemRequest.builder().id(1).description("request_description").requester(requester).build();
        List<ItemRequest> requests = List.of(request);
        Item item1 = Item.builder().id(1).name("item_name1").description("item_description1").available(true).itemRequest(request).build();
        List<Item> items = List.of(item1);

        when(itemRequestRepository.findRequestByRequesterIdOrderByCreatedDesc(requester.getId())).thenReturn(requests);
        when(itemRepository.findAllByRequestIdIn(requests)).thenReturn(items);

        ResponseItemRequestDto responseItemRequestDto = ResponseItemRequestDto.builder()
                .id(request.getId()).description(request.getDescription())
                .items(ItemMapper.toItemForRequestDto(items)).build();
        List<ResponseItemRequestDto> result = itemRequestService.getForOwner(requester.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0), responseItemRequestDto);
        verify(userRepository, times(1)).findById(requester.getId());
        verify(itemRequestRepository, times(1)).findRequestByRequesterIdOrderByCreatedDesc(requester.getId());
        verify(itemRepository, times(1)).findAllByRequestIdIn(requests);
    }

    @Test
    void findAll() {
        User requester = new User(2, "user2", "user2@mail.com");
        User user = new User(1, "user1", "user1@mail.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        ItemRequest request = ItemRequest.builder().id(1).description("request_description").requester(requester).build();
        Item item1 = Item.builder().id(1).name("item_name1").description("item_description1").available(true).itemRequest(request).build();
        List<Item> items = List.of(item1);
        List<ItemRequest> requests = List.of(request);
        Page<ItemRequest> requestsPage = new PageImpl<>(requests);
        Pageable page = PageRequest.of(0, 20, SORT_BY_START_DESC);
        when(itemRequestRepository.findAllForUser(eq(user.getId()), any(Pageable.class))).thenReturn(requestsPage);
        when(itemRepository.findAllByRequestIdIn(requests)).thenReturn(items);

        ResponseItemRequestDto responseItemRequestDto = ResponseItemRequestDto.builder()
                .id(request.getId()).description(request.getDescription())
                .items(ItemMapper.toItemForRequestDto(items)).build();
        List<ResponseItemRequestDto> result = itemRequestService.findAll(0, 20, user.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0), responseItemRequestDto);
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).findAllForUser(eq(user.getId()), any(Pageable.class));
        verify(itemRepository, times(1)).findAllByRequestIdIn(requests);
    }

    @Test
    void findByIdTest() {
        User user = new User(1, "user1", "user1@mail.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        ItemRequest request = ItemRequest.builder().id(1).description("request_description").requester(user).build();
        when(itemRequestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        Item item1 = Item.builder().id(1).name("item_name1").description("item_description1").available(true).itemRequest(request).build();
        List<Item> items = List.of(item1);
        when(itemRepository.findAllByItemRequest(request)).thenReturn(items);

        ResponseItemRequestDto responseItemRequestDto = ResponseItemRequestDto.builder()
                .id(request.getId()).description(request.getDescription())
                .items(ItemMapper.toItemForRequestDto(items)).build();
        ResponseItemRequestDto result = itemRequestService.findById(request.getId(), user.getId());

        assertNotNull(result);
        assertEquals(result, responseItemRequestDto);
        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).findById(request.getId());
        verify(itemRepository, times(1)).findAllByItemRequest(request);
    }

}
