package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    Item item;
    BookingResponseDto bookingResponseDto;
    BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        user = new User(1, "userName", "user@email.ru");
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
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).addBooking(anyInt(), any());
    }


   /* @PostMapping
    public BookingResponseDto createBooking(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @RequestBody BookingRequestDto bookingRequestDto
    ) {
        log.info("Вызван метод addBooking() в BookingController");
        return bookingService.addBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @PathVariable int bookingId,
            @RequestParam boolean approved
    ) {
        log.info("Вызван метод approveBooking() в BookingController");
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @PathVariable int bookingId
    ) {
        log.info("Вызван метод findBookingById() в BookingController");
        return bookingService.findBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getByUser(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @RequestParam(value = "state", defaultValue = "ALL")
            String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("Вызван метод findBookingByUser() в BookingController");
        return bookingService.findBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getByOwner(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @RequestParam(value = "state", defaultValue = "ALL")
            String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("Вызван метод findBookingByOwner() в BookingController");
        return bookingService.findBookingByOwner(userId, state, from, size);
    }*/




}
