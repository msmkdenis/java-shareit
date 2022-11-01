package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ItemResponseDto> findAll(int userId, int from, int size) {
        checkUser(userId);
        List<Item> itemList = itemRepository.findItemsByOwnerIdOrderById(userId, PageRequest.of(getPageNumber(from, size), size));
        List<ItemResponseDto> itemDtoResponseList = new ArrayList<>();
        for (Item item : itemList) {
            ItemResponseDto itemResponseDto = getItemResponseDto(item, userId);
            itemDtoResponseList.add(itemResponseDto);
        }
        log.info("Получены все вещи пользователя c id = {} (findAll())", userId);
        return itemDtoResponseList;
    }

    @Transactional
    @Override
    public CommentDto addComment(CommentDto commentDto, int userId, int itemId) {
        User user = checkUser(userId);
        Item item = checkItem(itemId);
        checkCommentAuthor(userId, itemId);
        if (commentDto.getText().isBlank()) {
            throw new ValidateException("Пустой отзыв! Должен быть текст!");
        }
        Comment comment = new Comment(commentDto.getId(),
                commentDto.getText(), item, user, LocalDateTime.now());
        log.info("Комментарий к вещи с id = {} пользователем с id = {} добавлен", itemId, userId);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemResponseDto findItemById(int itemId, int userId) {
        checkUser(userId);
        Item item = checkItem(itemId);
        log.info("Найдена вещь с id = {} (findItemById())", itemId);
        return getItemResponseDto(item, userId);
    }

    @Transactional
    public ItemDto addItem(ItemDto itemDto, int userId) {
        User owner = checkUser(userId);
        checkItemDto(itemDto);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = checkItemRequest(itemDto.getRequestId());
        }
        Item item = ItemMapper.toItem(itemDto, owner, request);
        itemRepository.save(item);
        log.info("Вещь с id = {} сохранена (addItem())", item.getId());
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    public ItemDto updateItem(ItemDto itemDto, int userId, int itemId) {
        User owner = checkUser(userId);
        Item oldItem = checkItem(itemId);
        if (oldItem.getOwner().equals(owner)) {
            if (itemDto.getName() != null) {
                oldItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                oldItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                oldItem.setAvailable(itemDto.getAvailable());
            }
        } else {
            throw new EntityNotFoundException("Ошибка! Редактировать информацию о вещи может только ее владелец");
        }
        log.info("Данные о вещи с id = {} обновлены (updateItem())", oldItem.getId());
        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Transactional
    public void deleteItem(ItemDto itemDto, int userId) {
        Item item = checkItem(itemDto.getId());
        User owner = checkUser(userId);
        if (!item.getOwner().equals(owner)) {
            throw new EntityNotFoundException("Ошибка! Удалить вещь может только ее владелец");
        } else {
            itemRepository.delete(item);
            log.info("Вещь с id = {} удалена", itemDto.getId());
        }
    }

    public List<ItemDto> search(String text, int userId, int from, int size) {
        userRepository.findById(userId);
        log.info("Поиск вещи с параметром text = {}", text);
        if (!text.isBlank()) {
            return itemRepository.searchItemsByText(text, PageRequest.of(getPageNumber(from, size), size))
                    .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private Item checkItem(int itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Ошибка! Вещь с id = %s не найдена!", itemId)));
    }

    private User checkUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(" Ошибка! " +
                        "Пользователь с id = %s не найден", id)));
    }

    private void checkItemDto(ItemDto itemDto) {
        String error = null;
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            error = "Ошибка! У вещи должно быть название!";
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            error = "Ошибка! У вещи должно быть описание!";
        }
        if (itemDto.getAvailable() == null) {
            error = "Ошибка! Отсутствует статус доступности вещи для аренды!";
        }
        if (error != null) {
            throw new ValidateException(error);
        }
    }

    private ItemResponseDto getItemResponseDto(Item item, int userId) {
        List<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking;
        Booking nextBooking;
        bookingList = bookingRepository.findAllByItemsId(item.getId());
        List<CommentDto> comments = commentRepository.findCommentsByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (bookingList.isEmpty()) {
            lastBooking = null;
            nextBooking = null;
        } else {
            lastBooking = bookingRepository.findLastBookingByItemId(item.getId(), userId, now)
                    .stream().findFirst().orElse(null);
            nextBooking = bookingRepository.findNextBookingByItemId(item.getId(), userId, now)
                    .stream().findFirst().orElse(null);
        }
        return ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, comments);
    }

    private void checkCommentAuthor(int userId, int itemId) {
        if (!(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now()))) {
            throw new ValidateException(
                    String.format("Пользователь id=%d не арендовал вещь id=%d или аренда не завершена!", userId, itemId)
            );
        }
    }

    private ItemRequest checkItemRequest(int id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ошибка! " +
                        "Запрос(request) с id = %s не найден", id)));
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }
}
