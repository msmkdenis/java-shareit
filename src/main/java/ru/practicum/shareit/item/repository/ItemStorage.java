package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    List<Item> findAll(User owner);

    Optional<Item> findById(int id);

    Item add(Item item, User owner);

    //редактировать можно только название, описание и статус доступа к аренде
    Item update(Item newItem, Item oldItem);

    void delete(Item item);

    List<Item> search(String text);
}
