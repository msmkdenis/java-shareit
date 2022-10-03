package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll().stream().map(UserMapper::toUser).collect(Collectors.toList());
    }

    @Override
    public UserDto findById(int id) {
        User user = checkUser(id);
        return UserMapper.toUser(user);
    }

    @Override
    public UserDto add(UserDto userDto) {
        User user = UserMapper.toUserDto(userDto);
        checkEmail(user);
        return UserMapper.toUser(userStorage.add(user));
    }

    @Override
    public UserDto update(UserDto userDto, int id) {
        User newUser = UserMapper.toUserDto(userDto);
        checkEmail(newUser);
        User oldUser = checkUser(id);
        return UserMapper.toUser(userStorage.update(newUser, oldUser));
    }

    @Override
    public void delete(int id) {
        User user = checkUser(id);
        userStorage.delete(user);
    }

    private void checkEmail(User user) {
        if (userStorage.contains(user.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует");
        }
    }

    private User checkUser(int id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с id = %s не найден", id)));
    }
}
