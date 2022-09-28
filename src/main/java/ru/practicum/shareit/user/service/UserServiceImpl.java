package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", id)));
    }

    public User add(UserDto userDto) {
        User user = UserMapper.toUserDto(userDto);
        checkEmail(user);
        return userRepository.add(user);
    }

    public User update(UserDto userDto, int id) {
        User newUser = UserMapper.toUserDto(userDto);
        checkEmail(newUser);
        User oldUser = findById(id);
        return userRepository.update(newUser, oldUser);
    }

    public void delete(int id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    public void checkEmail(User user) {
        if (userRepository.contains(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
    }
}
