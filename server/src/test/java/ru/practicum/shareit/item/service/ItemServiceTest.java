package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidateException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;

    @MockBean
    private final UserRepository userRepository;

    @MockBean
    private final ItemRepository itemRepository;

    @MockBean
    private final BookingRepository bookingRepository;

    @MockBean
    private final CommentRepository commentRepository;

    private final User user = new User(1, "userName", "user@email.ru");
    private final Item item = new Item(1, "item1", "description1", true, user, null);
    private final Comment comment = new Comment(1, "Comment", item, user,
            LocalDateTime.of(2022, 12, 12, 12, 12, 12));

    @Test
    void addItem() {
        when(itemRepository.save(any())).thenReturn(item);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        ItemDto itemDto = itemService.addItem(ItemMapper.toItemDto(item), user.getId());

        assertThat(itemDto.getId(), equalTo(item.getId()));
        assertThat(itemDto.getName(), equalTo(item.getName()));
        assertThat(itemDto.getDescription(), equalTo(item.getDescription()));
        assertThat(itemDto.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    void update() {
        Item newItem = new Item(item.getId(), "newItemName", "newItemDescription",
                item.getAvailable(), item.getOwner(), item.getRequest());
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(newItem)).thenReturn(newItem);

        ItemDto itemDto = ItemMapper.toItemDto(newItem);
        ItemDto newItemDto = itemService.updateItem(itemDto, user.getId(), itemDto.getId());

        assertNotNull(newItemDto);
        assertEquals(ItemDto.class, newItemDto.getClass());
        assertEquals(newItem.getId(), newItemDto.getId());
        assertEquals(newItem.getName(), newItemDto.getName());
        assertEquals(newItem.getDescription(), newItemDto.getDescription());
        assertEquals(newItem.getAvailable(), newItemDto.getAvailable());

    }

    @Test
    void deleteItem() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        itemService.deleteItem(ItemMapper.toItemDto(item), user.getId());
        List<Item> items = itemRepository.findAll();

        assertEquals(0, items.size());
    }

    @Test
    void deleteItemByNonOwner() {
        User newUser = new User(2, "userName", "user@email.ru");
        item.setOwner(newUser);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        assertThrows(EntityNotFoundException.class,
                () -> itemService.deleteItem(ItemMapper.toItemDto(item), user.getId()));
    }

    @Test
    void findById() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemsId(anyInt())).thenReturn(Collections.emptyList());
        when(commentRepository.findCommentsByItemId(anyInt())).thenReturn(Collections.emptyList());

        ItemResponseDto itemDto = ItemMapper.toItemResponseDto(item,
                null,
                null,
                Collections.emptyList());
        ItemResponseDto newItemDto = itemService.findItemById(item.getId(), user.getId());

        assertNotNull(newItemDto);
        assertEquals(ItemResponseDto.class, newItemDto.getClass());
        assertEquals(itemDto.getId(), newItemDto.getId());
        assertEquals(itemDto.getName(), newItemDto.getName());
        assertEquals(itemDto.getDescription(), newItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), newItemDto.getAvailable());
    }

    @Test
    void getAllItemsByOwner() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findItemsByOwnerIdOrderById(user.getId(), Pageable.ofSize(15)))
                .thenReturn(Collections.singletonList(item));
        when(bookingRepository.findAllByItemsId(anyInt())).thenReturn(Collections.emptyList());
        when(commentRepository.findCommentsByItemId(anyInt())).thenReturn(Collections.emptyList());

        List<ItemResponseDto> itemsList = itemService.findAll(user.getId(),0,15);

        assertNotNull(itemsList);
        assertEquals(1, itemsList.size());
        assertEquals(item.getId(), itemsList.get(0).getId());
        assertEquals(item.getName(), itemsList.get(0).getName());
        assertEquals(item.getDescription(), itemsList.get(0).getDescription());
        assertEquals(item.getAvailable(), itemsList.get(0).getAvailable());
    }

    @Test
    void search() {
        when(itemRepository.searchItemsByText("text", PageRequest.ofSize(15)))
                .thenReturn(Collections.singletonList(item));

        List<ItemDto> items = itemService.search("text", user.getId(), 0, 15);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(item.getName(), items.get(0).getName());
        assertEquals(item.getDescription(), items.get(0).getDescription());
        assertEquals(item.getAvailable(), items.get(0).getAvailable());
    }

    @Test
    void addComment() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(anyInt(), anyInt(), any()))
                .thenReturn(Boolean.TRUE);
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto commentDto = itemService.addComment(CommentMapper.toCommentDto(comment),
                user.getId(), item.getId());

        assertNotNull(commentDto);
        assertEquals(CommentDto.class, commentDto.getClass());
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(user.getName(), commentDto.getAuthorName());
    }

    @Test
    void addBlankComment() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(anyInt(), anyInt(), any()))
                .thenReturn(Boolean.TRUE);
        when(commentRepository.save(any())).thenReturn(comment);
        comment.setText("");

        assertThrows(ValidateException.class,() -> itemService.addComment(CommentMapper.toCommentDto(comment),
                user.getId(), item.getId()));
    }

    @Test
    void addItemWithWrongUserId() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertThrows(EntityNotFoundException.class, () -> itemService.addItem(itemDto, 999));
    }

    @Test
    void updateItemWithWrongUserId() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDto, 999, 1));
    }

    @Test
    void updateItemWithWrongItemId() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDto, 1, 999));
    }

    @Test
    void addItemWithWrongItemName() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setName("");
        assertThrows(ValidateException.class, () -> itemService.addItem(itemDto, user.getId()));
    }

    @Test
    void addItemWithWrongItemDescription() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setDescription("");
        assertThrows(ValidateException.class, () -> itemService.addItem(itemDto, user.getId()));
    }

    @Test
    void getItemResponseDto() {
        Booking lastBooking = new Booking(1, LocalDateTime.of(2022, 10, 14, 13, 22, 22),
                LocalDateTime.of(2022, 9, 15, 13, 22, 22),
                item, user, BookingStatus.APPROVED);

        Booking nextBooking = new Booking(1, LocalDateTime.of(2022, 10, 16, 13, 22, 22),
                LocalDateTime.of(2022, 9, 17, 13, 22, 22),
                item, user, BookingStatus.APPROVED);

        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemsId(anyInt())).thenReturn(List.of(lastBooking));
        when(commentRepository.findCommentsByItemId(anyInt())).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastBookingByItemId(anyInt(), anyInt(), any()))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findNextBookingByItemId(anyInt(), anyInt(), any()))
                .thenReturn(List.of(nextBooking));
        ItemResponseDto itemDto = ItemMapper.toItemResponseDto(item,
                lastBooking,
                nextBooking,
                Collections.emptyList());
        ItemResponseDto findItemDto = itemService.findItemById(item.getId(), user.getId());
        assertNotNull(findItemDto);
        assertEquals(ItemResponseDto.class, findItemDto.getClass());
        assertEquals(itemDto.getId(), findItemDto.getId());
        assertEquals(itemDto.getName(), findItemDto.getName());
        assertEquals(itemDto.getDescription(), findItemDto.getDescription());
        assertEquals(itemDto.getAvailable(), findItemDto.getAvailable());
        assertEquals(itemDto.getLastBooking(), findItemDto.getLastBooking());
        assertEquals(itemDto.getNextBooking(), findItemDto.getNextBooking());
    }
}
