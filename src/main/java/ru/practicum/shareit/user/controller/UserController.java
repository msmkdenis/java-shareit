package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        log.info("Вызван метод findAll() в UserController");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable int userId) {
        log.info("Вызван метод findById() в UserController");
        return userService.findById(userId);
    }

    @PostMapping
    public UserDto addUser(
            @Validated({Create.class})
            @RequestBody UserDto user
    ) {
        log.info("Вызван метод addUser() в UserController");
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(
            @Validated({Update.class})
            @RequestBody UserDto user,
            @PathVariable int userId
    ) {
        log.info("Вызван метод updateUser() в UserController");
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable int userId) {
        log.info("Вызван метод deleteUser() в UserController");
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
