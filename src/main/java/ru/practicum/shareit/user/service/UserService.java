package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(int id);

    User save(UserDto user);

    User edit(UserDto user, int id);

    void delete(int id);
}
