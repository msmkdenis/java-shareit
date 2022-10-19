package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    List<ItemResponseDto> findAll(int userId);

    ItemResponseDto findItemById(int itemId, int userId);

    ItemDto addItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto item, int userId, int id);

    void deleteItem(int id);

    List<ItemDto> search(String text, int userId);

    CommentDto addComment(CommentDto commentDto, int userId, int itemId);
}
