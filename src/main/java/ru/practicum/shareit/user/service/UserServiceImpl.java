package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        log.info("Получен список всех пользователей (findAll())");
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findById(int id) {
        User user = checkUser(id);
        log.info("Получен пользователь с id = {}", id);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        log.info("Пользователь с id = {} создан", user.getId());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, int id) {
        User oldUser = checkUser(id);
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            oldUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            oldUser.setEmail(userDto.getEmail());
        }
        log.info("Данные пользователя с id = {} обновлены", oldUser.getId());
        return UserMapper.toUserDto(userRepository.save(oldUser));
    }

    @Transactional
    @Override
    public void deleteUser(int id) {
        checkUser(id);
        userRepository.deleteById(id);
        log.info("Пользователь с id = {} удален", id);
    }

    private User checkUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ошибка! " +
                        "Пользователь с id = %s не найден", id)));
    }
}
