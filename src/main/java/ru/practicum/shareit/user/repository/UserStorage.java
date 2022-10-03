package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    Optional<User> findById(int id);

    User add(User user);

    User update(User newUser, User oldUser);

    void delete(User user);

    boolean contains(String email);
}
