package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.PostItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.TestUtils.item;
import static ru.practicum.shareit.utils.Constants.USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private PostItemDto postItemDto;
    private ResponseItemDto responseItemDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        postItemDto = PostItemDto.builder().id(item.getId()).name(item.getName()).description(item.getDescription())
                .available(item.getAvailable()).requestId(item.getItemRequest().getId()).build();
        responseItemDto = ResponseItemDto.builder().id(item.getId()).name(item.getName()).description(item.getDescription())
                .available(item.getAvailable()).requestId(item.getItemRequest().getId()).build();
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.addItem(any(), any(Integer.class))).thenReturn(item);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(postItemDto))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(postItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(postItemDto.getName())))
                .andExpect(jsonPath("$.description", is(postItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(postItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(postItemDto.getRequestId())));
    }

    @Test
    void createCommentTest() throws Exception {
        Comment comment = Comment.builder().id(1).text("text").created(LocalDateTime.now()).build();
        ResponseCommentDto commentResponseDto = ResponseCommentDto.builder()
                .id(comment.getId()).text(comment.getText()).created(comment.getCreated()).build();
        when(itemService.createComment(any(), any(Integer.class), any(Integer.class))).thenReturn(commentResponseDto);
        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(comment))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Integer.class))
                .andExpect(jsonPath("$.created", is(notNullValue())))
                .andExpect(jsonPath("$.text", is(comment.getText())));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(any(), any(Integer.class))).thenReturn(item);
        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(postItemDto))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(postItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(postItemDto.getName())))
                .andExpect(jsonPath("$.description", is(postItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(postItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(postItemDto.getRequestId())));
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemForUser(any(Integer.class), any(Integer.class))).thenReturn(responseItemDto);
        mvc.perform(get("/items/1")
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(postItemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(postItemDto.getName())))
                .andExpect(jsonPath("$.description", is(postItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(postItemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(postItemDto.getRequestId())));
    }

    @Test
    void getAllItemsOwnerTest() throws Exception {
        when(itemService.getAll(any(Integer.class), eq(0), eq(20))).thenReturn(List.of(responseItemDto));
        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(postItemDto.getId())))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(postItemDto.getName())))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder(postItemDto.getDescription())))
                .andExpect(jsonPath("$[*].available", containsInAnyOrder(postItemDto.getAvailable())))
                .andExpect(jsonPath("$[*].requestId", containsInAnyOrder(postItemDto.getRequestId())));
    }

    @Test
    void getAllItemsOwnerWithPagination() throws Exception {
        when(itemService.getAll(any(Integer.class), any(Integer.class), any(Integer.class))).thenReturn(List.of(responseItemDto));
        mvc.perform(get("/items?from=0&size=20")
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(postItemDto.getId())))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(postItemDto.getName())))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder(postItemDto.getDescription())))
                .andExpect(jsonPath("$[*].available", containsInAnyOrder(postItemDto.getAvailable())))
                .andExpect(jsonPath("$[*].requestId", containsInAnyOrder(postItemDto.getRequestId())));
    }

    @Test
    void searchItemByTextTest() throws Exception {
        when(itemService.findItemsByText(any(), eq(0), eq(20))).thenReturn(List.of(responseItemDto));
        mvc.perform(get("/items/search?text=text")
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(postItemDto.getId())))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(postItemDto.getName())))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder(postItemDto.getDescription())))
                .andExpect(jsonPath("$[*].available", containsInAnyOrder(postItemDto.getAvailable())))
                .andExpect(jsonPath("$[*].requestId", containsInAnyOrder(postItemDto.getRequestId())));
    }

    @Test
    void searchItemByTextTestWithPagination() throws Exception {
        when(itemService.findItemsByText(any(), any(Integer.class), any(Integer.class))).thenReturn(List.of(responseItemDto));
        mvc.perform(get("/items/search?text=text&from=0&size=20")
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(postItemDto.getId())))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(postItemDto.getName())))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder(postItemDto.getDescription())))
                .andExpect(jsonPath("$[*].available", containsInAnyOrder(postItemDto.getAvailable())))
                .andExpect(jsonPath("$[*].requestId", containsInAnyOrder(postItemDto.getRequestId())));
    }
}