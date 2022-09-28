package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(int id);

    User add(UserDto user);

    User update(UserDto user, int id);

    void delete(int id);
}
