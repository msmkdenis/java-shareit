package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTest {

    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    User user;
    User owner;
    Item item;
    BookingResponseDto bookingResponseDto;
    BookingRequestDto bookingRequestDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "userName", "user@email.ru");
        owner = new User(2, "ownerName", "owner@email");
        item = new Item(1, "item1", "description1", true, user, null);
        bookingResponseDto = new BookingResponseDto(
                1,
                LocalDateTime.of(2022, 12, 12, 12, 12, 12),
                LocalDateTime.of(2022, 12, 15, 12, 12, 12),
                BookingStatus.WAITING,
                user,
                item);
        bookingRequestDto = new BookingRequestDto(
                1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
    }

    @Test
    void createBooking() throws Exception {
        when(bookingService.addBooking(anyInt(), any())).thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto.getBooker()), User.class))
                .andExpect(jsonPath("$.item", is(bookingResponseDto.getItem()), Item.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).addBooking(anyInt(), any());
    }

    @Test
    void createBookingWithEndIsAfterStart() throws Exception {
        bookingRequestDto.setStart(LocalDateTime.now().minusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().minusDays(4));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(r -> assertTrue(r.getResolvedException() instanceof MethodArgumentNotValidException));

        verify(bookingService, times(0)).addBooking(anyInt(), any());
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingResponseDto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto.getBooker()), User.class))
                .andExpect(jsonPath("$.item", is(bookingResponseDto.getItem()), Item.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).approveBooking(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    void getById() throws Exception {
        when(bookingService.findBookingById(anyInt(), anyInt())).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingResponseDto.getId())
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.booker", is(bookingResponseDto.getBooker()), User.class))
                .andExpect(jsonPath("$.item", is(bookingResponseDto.getItem()), Item.class));

        verify(bookingService, times(1)).findBookingById(anyInt(), anyInt());
    }

    @Test
    void getByUser() throws Exception {
        when(bookingService.findBookingByUser(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .content(mapper.writeValueAsString(bookingResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].booker", is(bookingResponseDto.getBooker()), User.class))
                .andExpect(jsonPath("$[0].item", is(bookingResponseDto.getItem()), Item.class));

        verify(bookingService, times(1))
                .findBookingByUser(anyInt(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getAllByOwnerId() throws Exception {
        when(bookingService.findBookingByOwner(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "5")
                        .content(mapper.writeValueAsString(bookingResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].booker", is(bookingResponseDto.getBooker()), User.class))
                .andExpect(jsonPath("$[0].item", is(bookingResponseDto.getItem()), Item.class));

        verify(bookingService, times(1))
                .findBookingByOwner(anyInt(), anyString(), anyInt(), anyInt());
    }
}
