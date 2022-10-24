package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
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
    }
}
