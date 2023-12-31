package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.TestUtils.*;
import static ru.practicum.shareit.utils.Constants.USER_ID_HEADER;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    BookingController bookingController;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private PostBookingDto postBookingDto;
    private ResponseBookingDto responseBookingDto;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        postBookingDto = new PostBookingDto(item.getId(), booking.getStart(), booking.getEnd());
        responseBookingDto = BookingMapper.toResponseBookingDto(booking);

    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.addBooking(any(), any(Integer.class))).thenReturn(booking);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(postBookingDto))
                        .header(USER_ID_HEADER, booker.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(responseBookingDto.getStatus().toString())));
    }

    @Test
    void patchBookingTest() throws Exception {
        when(bookingService.approve(any(Integer.class), any(Boolean.class), any(Integer.class)))
                .thenReturn(booking);
        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(USER_ID_HEADER, owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(responseBookingDto.getStatus().toString())));
    }

    @Test
    void findByIdTest() throws Exception {
        when(bookingService.getBookingForUser(any(Integer.class), any(Integer.class)))
                .thenReturn(booking);

        mvc.perform(get("/bookings/1")
                        .header(USER_ID_HEADER, booker.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseBookingDto.getId()), Integer.class))
                .andExpect(jsonPath("$.start", is(notNullValue())))
                .andExpect(jsonPath("$.end", is(notNullValue())))
                .andExpect(jsonPath("$.status", is(responseBookingDto.getStatus().toString())));
    }

    @Test
    void getAllBookingsTest() throws Exception {
        when(bookingService.getAllBookings(any(), any(Integer.class), any(Integer.class), any(Integer.class))).thenReturn(List.of(booking));
        BookingState state = BookingState.ALL;
        mvc.perform(get("/bookings?state={state}", state)
                        .content(mapper.writeValueAsString(booking))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(responseBookingDto.getId())))
                .andExpect(jsonPath("$[*].start", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].end", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].status", containsInAnyOrder(responseBookingDto.getStatus().toString())));
    }

    @Test
    void getAllBookingsTestWithPagination() throws Exception {
        when(bookingService.getAllBookings(any(), any(Integer.class), any(Integer.class), any(Integer.class))).thenReturn(List.of(booking));
        BookingState state = BookingState.ALL;
        mvc.perform(get("/bookings?state={state}", state)
                        .content(mapper.writeValueAsString(booking))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(responseBookingDto.getId())))
                .andExpect(jsonPath("$[*].start", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].end", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].status", containsInAnyOrder(responseBookingDto.getStatus().toString())));
    }

    @Test
    void getAllBookingsForOwnerTest() throws Exception {
        when(bookingService.getAllBookingForOwner(any(), any(Integer.class), any(Integer.class), any(Integer.class))).thenReturn(List.of(booking));
        BookingState state = BookingState.ALL;
        mvc.perform(get("/bookings/owner?state={state}", state)
                        .content(mapper.writeValueAsString(booking))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(responseBookingDto.getId())))
                .andExpect(jsonPath("$[*].start", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].end", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].status", containsInAnyOrder(responseBookingDto.getStatus().toString())));
    }

    @Test
    void getAllBookingsForOwnerWithPaginationTest() throws Exception {
        when(bookingService.getAllBookingForOwner(any(), any(Integer.class), any(Integer.class), any(Integer.class))).thenReturn(List.of(booking));
        BookingState state = BookingState.ALL;
        mvc.perform(get("/bookings/owner?state={state}&from=0&size=10", state)
                        .content(mapper.writeValueAsString(booking))
                        .header(USER_ID_HEADER, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(responseBookingDto.getId())))
                .andExpect(jsonPath("$[*].start", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].end", containsInAnyOrder(notNullValue())))
                .andExpect(jsonPath("$[*].status", containsInAnyOrder(responseBookingDto.getStatus().toString())));
    }

}
