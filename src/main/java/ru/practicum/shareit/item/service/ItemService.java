package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> findAll(int userId);

    ItemDto findById(int id);

    ItemDto add(ItemDto itemDto, int userId);

    ItemDto update(ItemDto item, int userId, int id);

    void delete(int id);

    List<Item> search(String text, int userId);
}
