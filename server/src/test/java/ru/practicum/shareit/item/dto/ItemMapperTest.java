package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    User user;
    ItemRequest itemRequest;
    Item item;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "userName", "user@email.ru");
        itemRequest = new ItemRequest(1, "ru/practicum/shareit/request", user,
                LocalDateTime.of(2022, 12, 12, 12, 12, 12));
        item = new Item(1, "ru/practicum/shareit/request/item", "description", true, user, itemRequest);
    }

    @Test
    void toItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        assertNotNull(itemDto);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        Assertions.assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void toItem() {
        Item newItem = ItemMapper.toItem(ItemMapper.toItemDto(item), user, itemRequest);
        assertNotNull(newItem);
        assertEquals(item.getId(), newItem.getId());
        assertEquals(item.getName(), newItem.getName());
        assertEquals(item.getDescription(), newItem.getDescription());
        assertEquals(item.getAvailable(), newItem.getAvailable());
        Assertions.assertEquals(itemRequest.getId(), newItem.getRequest().getId());
        assertEquals(user.getId(), newItem.getOwner().getId());
    }

    @Test
    void toItemResponseDto() {
        ItemResponseDto responseDto = ItemMapper.toItemResponseDto(item, null, null, new ArrayList<>());
        assertNotNull(responseDto);
        assertEquals(item.getId(), responseDto.getId());
        assertEquals(item.getName(), responseDto.getName());
        assertEquals(item.getDescription(), responseDto.getDescription());
        assertEquals(item.getAvailable(), responseDto.getAvailable());
        assertNull(responseDto.getLastBooking());
        assertNull(responseDto.getNextBooking());
        assertEquals(0, responseDto.getComments().size());
    }
}
