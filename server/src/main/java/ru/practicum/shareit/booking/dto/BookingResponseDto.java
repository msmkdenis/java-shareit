package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingResponseDto {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private User booker;
    private Item item;
}
