package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.PostItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationItemRequestServiceTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Test
    void getForOwner() {
        User createdOwner = userService.createUser(User.builder().name("user").email("user@mail.com").build());
        User createdRequestor = userService.createUser(User.builder().name("user2").email("user2@mail.com").build());
        Item createdItem = itemService.addItem(PostItemDto.builder().name("item1").description("description1").available(true).build(), createdOwner.getId());

        PostItemRequestDto postItemRequestDto = PostItemRequestDto.builder().description("request_descritpion").build();
        ItemRequest request = ItemRequestMapper.toItemRequest(postItemRequestDto);
        request = itemRequestService.createItemRequest(request, createdRequestor.getId());

        ResponseItemRequestDto gottenItemRequest = itemRequestService.findById(request.getId(), createdRequestor.getId());

        //TypedQuery<Booking> query = em.createQuery("Select b from bookings b where b.id = :bookingId", Booking.class);

        TypedQuery<ItemRequest> query = em.createQuery("Select i from requests i where i.id = :requestId", ItemRequest.class);
        ItemRequest gottenItemRequestFromDB = query
                .setParameter("requestId", request.getId())
                .getSingleResult();

        assertThat(gottenItemRequest.getId(), equalTo(gottenItemRequestFromDB.getId()));
        assertThat(createdRequestor, equalTo(gottenItemRequestFromDB.getRequester()));
        assertThat(gottenItemRequest.getCreated(), equalTo(gottenItemRequestFromDB.getCreated()));
        //assertThat(gottenItemRequest.getStart(), equalTo(gottenItemRequestFromDB.getStart()));
        //assertThat(gottenItemRequest.getEnd(), equalTo(gottenItemRequestFromDB.getEnd()));


        userService.deleteUser(createdOwner.getId());
        userService.deleteUser(createdRequestor.getId());


    }


}
