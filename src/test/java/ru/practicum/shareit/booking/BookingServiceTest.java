package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.MessageFailedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    BookingService bookingService;
    @MockBean
    BookingRepository bookingRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    ItemRepository itemRepository;
    Item item;
    User user;
    User owner;
    Booking booking;
    BookingResponseDto bookingResponseDto;
    BookingRequestDto bookingRequestDto;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
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
        bookingResponseDto = new BookingResponseDto(
                1,
                LocalDateTime.of(2022, 12, 12, 12, 12, 12),
                LocalDateTime.of(2022, 12, 15, 12, 12, 12),
                BookingStatus.WAITING,
                user,
                item);
        bookingRequestDto = new BookingRequestDto(
                1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        bookingResponseDto = new BookingResponseDto(1, booking.getStart(),
                booking.getEnd(), booking.getStatus(), user, item);
        bookingRequestDto = new BookingRequestDto(bookingResponseDto.getItem().getId(),
                bookingResponseDto.getStart(), bookingResponseDto.getEnd());
    }

    @Test
    void addBooking() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto bookingDto = bookingService.addBooking(user.getId(), bookingRequestDto);
        assertNotNull(bookingDto);
        assertEquals(BookingResponseDto.class, bookingDto.getClass());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void addBookingWithEmptyUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        Exception ex = assertThrows(EntityNotFoundException.class,
                ()-> bookingService.addBooking(user.getId(), bookingRequestDto));
        assertEquals("Ошибка! Пользователь с id = 1 не найден", ex.getMessage());
    }

    @Test
    void addBookingWhenEndIsBeforeStart() {
        bookingRequestDto.setEnd(booking.getStart().minusDays(10));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> bookingService.addBooking(user.getId(), bookingRequestDto));
    }

    @Test
    void addBookingWhenStartIsBeforeNow() {
        bookingRequestDto.setStart(LocalDateTime.now().minusDays(1));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class,
                () -> bookingService.addBooking(user.getId(), bookingRequestDto));
    }

    @Test
    void addBookingWhenEndIsBeforeNow() {
        bookingRequestDto.setStart(LocalDateTime.now().minusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().minusDays(1));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(user.getId(), bookingRequestDto));
    }

    @Test
    void findBookingById() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        BookingResponseDto bookingResponseDto = bookingService.findBookingById(owner.getId(), booking.getId());

        assertNotNull(bookingResponseDto);
        assertEquals(booking.getId(), bookingResponseDto.getId());
        assertEquals(booking.getBooker(), bookingResponseDto.getBooker());
    }

    @Test
    void findBookingByWrongId() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.findBookingById(owner.getId(), booking.getId()));
    }

    @Test
    void approveBooking() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto bookingResponseDto = bookingService.approveBooking(owner.getId(),
                booking.getId(), true);
        assertNotNull(bookingResponseDto);
        assertEquals(BookingResponseDto.class, bookingResponseDto.getClass());
        assertEquals(booking.getId(), bookingResponseDto.getId());
        assertEquals(booking.getBooker().getId(), bookingResponseDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingResponseDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingResponseDto.getStatus());
    }

    @Test
    void approveBookingWithWrongState() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        booking.setStatus(BookingStatus.APPROVED);

        assertThrows(BookingStateException.class, ()-> bookingService.approveBooking(owner.getId(),
                booking.getId(), true));
    }

    @Test
    void approveBookingWithWrongOwner() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.approveBooking(user.getId(), booking.getId(), true));
    }

    @Test
    void findBookingByUser() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyInt(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyInt(),
                any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyInt(),
                any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyInt(),
                any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(),
                any())).thenReturn(List.of(booking));

        List<BookingResponseDto> bookings = bookingService.findBookingByUser(user.getId(), "ALL",
                1,10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByUser(user.getId(), "CURRENT",
                1,10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByUser(user.getId(), "PAST",
                1,10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByUser(user.getId(), "FUTURE",
                1,10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByUser(user.getId(), "WAITING",
                1,10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByUser(user.getId(), "REJECTED",
                1,10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByUser(user.getId(), "ALL",
                1,10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        MessageFailedException exception = assertThrows(MessageFailedException.class, () ->
                bookingService.findBookingByUser(user.getId(), "UNSUPPORTED_STATUS", 1,10));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void findBookingByOwner() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemsOwnerId(anyInt(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllCurrentByItemsOwnerId(anyInt(),
                any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllPastByItemsOwnerId(anyInt(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllFutureByItemsOwnerId(anyInt(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllStatusByItemsOwnerId(anyInt(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> bookings = bookingService.findBookingByOwner(user.getId(), "ALL",
                1, 10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByOwner(user.getId(), "CURRENT",
                1, 10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByOwner(user.getId(), "PAST",
                1, 10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByOwner(user.getId(), "FUTURE",
                1, 10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByOwner(user.getId(), "WAITING",
                1, 10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByOwner(user.getId(), "REJECTED",
                1, 10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.findBookingByOwner(user.getId(), "ALL",
                1, 10);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        MessageFailedException exception = assertThrows(MessageFailedException.class, () ->
                bookingService.findBookingByOwner(user.getId(), "UNSUPPORTED_STATUS", 1,10));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }
}
