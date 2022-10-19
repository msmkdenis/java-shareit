package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

import static java.util.Objects.isNull;

public class BookingMapper {

    public static Booking toBooking(BookingRequestDto bookingRequestDto) {
        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());

        return booking;
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        if (isNull(booking)) {
            return null;
        }
        return new BookingItemDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBooker(),
                booking.getItem()
        );
    }
}
