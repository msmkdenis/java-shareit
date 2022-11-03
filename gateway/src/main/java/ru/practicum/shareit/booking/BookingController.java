package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {

    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @Validated @RequestBody BookingRequestDto bookingRequestDto
    ) {
        checkStartAndEnd(bookingRequestDto);
        log.info("Вызван метод addBooking() в BookingController");
        return bookingClient.createBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @PathVariable int bookingId,
            @RequestParam boolean approved
    ) {
        log.info("Вызван метод approveBooking() в BookingController");
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @PathVariable int bookingId
    ) {
        log.info("Вызван метод findBookingById() в BookingController");
        return bookingClient.findBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUser(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @RequestParam(value = "state", defaultValue = "ALL")
            String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("Вызван метод findBookingByUser() в BookingController");
        return bookingClient.findBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @RequestParam(value = "state", defaultValue = "ALL")
            String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("Вызван метод findBookingByOwner() в BookingController");
        return bookingClient.findBookingByOwner(userId, state, from, size);
    }

    private void checkStartAndEnd(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new ValidationException("Ошибка! Дата окончания не может быть ранее даты старта!");
        }
        if (bookingRequestDto.getStart().isEqual(bookingRequestDto.getEnd())) {
            throw new ValidationException("Ошибка! Дата начала не может быть равна дате окончания!");
        }
    }
}
