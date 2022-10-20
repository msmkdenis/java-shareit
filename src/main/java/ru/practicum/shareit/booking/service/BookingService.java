package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto addBooking(int userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto approveBooking(int userId, int bookingId, boolean approve);

    BookingResponseDto findBookingById(int userId, int bookingId);

    List<BookingResponseDto> findBookingByUser(int userId, String state);

    List<BookingResponseDto> findBookingByOwner(int ownerId, String state);
}
