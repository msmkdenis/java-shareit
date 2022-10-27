package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingMapperTest {

    User user;
    User owner;
    Item item;
    Booking booking;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "userName", "user@email.ru");
        owner = new User(2, "ownerName", "owner@email");
        item = new Item(1, "item1", "description1", true, owner, null);
        booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item,
                user,
                BookingStatus.WAITING.WAITING);
    }

    @Test
    void toBookingItemDto() {
        BookingItemDto bookingDto = BookingMapper.toBookingItemDto(booking);
        assertNotNull(bookingDto);
        assertEquals(BookingItemDto.class, bookingDto.getClass());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
    }
}
