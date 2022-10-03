package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findAll(int userId);

    ItemDto findById(int id);

    ItemDto add(ItemDto itemDto, int userId);

    ItemDto update(ItemDto item, int userId, int id);

    void delete(int id);

    List<ItemDto> search(String text, int userId);
}
