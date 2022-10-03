package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(int id);

    UserDto add(UserDto user);

    UserDto update(UserDto user, int id);

    void delete(int id);
}
