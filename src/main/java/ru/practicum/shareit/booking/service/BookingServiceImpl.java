package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingResponseDto addBooking(int userId, BookingRequestDto bookingRequestDto) {
        User booker = checkUser(userId);
        Item item = checkItem(bookingRequestDto.getItemId());
        checkStartAndEnd(bookingRequestDto);
        checkItemOwner(userId, item);
        checkItemAvailable(item);
        Booking booking = BookingMapper.toBooking(bookingRequestDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        log.info("Запрос Booking с id = {} сохранен (addBooking())", booking.getId());
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto approveBooking(int userId, int bookingId, boolean approve) {
        checkUser(userId);
        Booking booking = checkBooking(bookingId);
        checkBookingStatus(booking);
        Item item = booking.getItem();
        checkAccessForApprove(userId, item);
        booking.setStatus(approve ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Статус бронированиня у запроса с id = {} изменен на {} (approveBooking())",
                booking.getId(), booking.getStatus());
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingResponseDto findBookingById(int userId, int bookingId) {
        Booking booking = checkBooking(bookingId);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new EntityNotFoundException("Ошибка! " +
                    "Поиск запроса на бронирование по id возможен только для автора запроса или для владельца!");
        }
        log.info("Найден запрос с id = {} (findBookingById())", booking.getId());
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> findBookingByUser(int userId, String state) {
        checkUser(userId);
        validBookingState(state);
        LocalDateTime now = LocalDateTime.now();
        List<BookingResponseDto> bookingList = null;
        switch (BookingState.valueOf(state)) {
            case CURRENT:
                bookingList = bookingRepository
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookingList = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookingList = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookingList = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
            case ALL:
                bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
        }
        log.info("Получены все бронирования пользователя с id = {} (findBookingByUser())", userId);
        return bookingList;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> findBookingByOwner(int ownerId, String state) {
        checkUser(ownerId);
        validBookingState(state);
        LocalDateTime now = LocalDateTime.now();
        List<BookingResponseDto> bookingList = null;
        switch (BookingState.valueOf(state)) {
            case CURRENT:
                bookingList = bookingRepository.findAllCurrentByItemsOwnerId(ownerId, now, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
            case PAST:
                bookingList = bookingRepository.findAllPastByItemsOwnerId(ownerId, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
            case FUTURE:
                bookingList = bookingRepository.findAllFutureByItemsOwnerId(ownerId, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
            case WAITING:
                bookingList = bookingRepository.findAllStatusByItemsOwnerId(ownerId, BookingStatus.WAITING)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
            case REJECTED:
                bookingList = bookingRepository.findAllStatusByItemsOwnerId(ownerId, BookingStatus.REJECTED)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
            case ALL:
                bookingList = bookingRepository.findAllByItemsOwnerId(ownerId).stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
                break;
        }
        log.info("Получены все бронирования пользователя с id = {} (findBookingByOwner())", ownerId);
        return bookingList;
    }

    private void checkBookingStatus(Booking booking) {
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingStateException(String.format("Ошибка! Бронирование id=%d уже находится в статусе %S!",
                    booking.getId(), booking.getStatus())
            );
        }
    }

    private void checkItemOwner(int userId, Item item) {
        if (userId == item.getOwner().getId()) {
            throw new EntityNotFoundException(String.format("Ошибка! Пользователь id=%d является " +
                    "владельцем вещи id=%d", userId, item.getId()));
        }
    }

    private void checkAccessForApprove(int userId, Item item) {
        if (item.getOwner().getId() != userId) {
            throw new EntityNotFoundException(String.format("Ошибка! Пользователь" +
                    " id=%d не является владельцем вещи id=%d!", userId, item.getId()));
        }
    }

    private void checkItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Ошибка! Вещь id=%d не доступна для бронирования!", item.getId()));
        }
    }

    private void checkStartAndEnd(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new ValidationException("Ошибка! Дата окончания не может быть раньше даты старта!");
        }
        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Ошибка! Дата начала не может быть раньше текущей даты!");
        }
        if (bookingRequestDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Ошибка! Дата окончания не может быть раньше текущей даты!");
        }
    }

    private User checkUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ошибка! " +
                        "Пользователь с id = %s не найден", id)));
    }

    private Item checkItem(int id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ошибка! " +
                        "Вещь с id = %s не найдена", id)));
    }

    private Booking checkBooking(int id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ошибка! " +
                        "Бронирование с id = %s не найдена", id)));
    }

    private void validBookingState(String state) {
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new MessageFailedException(String.format("Unknown state: %s", state));
        }
    }
}
