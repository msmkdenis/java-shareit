package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        log.info("Вызван метод findAll() в UserController");
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User findById(@PathVariable int userId) {
        log.info("Вызван метод findById() в UserController");
        return userService.findById(userId);
    }

    @PostMapping
    public User saveUser(@Valid @RequestBody UserDto user) {
        log.info("Вызван метод saveUser() в UserController");
        return userService.save(user);
    }

    @PatchMapping("/{userId}")
    public User editUser(@RequestBody UserDto user, @PathVariable int userId) {
        log.info("Вызван метод editUser() в UserController");
        return userService.edit(user, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable int userId) {
        log.info("Вызван метод delete() в UserController");
        userService.delete(userId);
        return ResponseEntity.ok().build();
    }
}
