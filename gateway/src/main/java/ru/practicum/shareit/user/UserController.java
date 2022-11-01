package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Вызван метод findAll() в UserController");
        return userClient.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable int userId) {
        log.info("Вызван метод findById() в UserController");
        return userClient.findById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(
            @Validated({Create.class})
            @RequestBody UserDto user
    ) {
        log.info("Вызван метод addUser() в UserController");
        return userClient.addUser(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @Validated({Update.class})
            @RequestBody UserDto user,
            @PathVariable int userId
    ) {
        log.info("Вызван метод updateUser() в UserController");
        return userClient.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId) {
        log.info("Вызван метод deleteUser() в UserController");
        userClient.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
