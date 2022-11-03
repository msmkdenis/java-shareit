package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> findAll(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Вызван метод findAll() в ItemController");
        return itemClient.findAll(userId, from, size);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> findById(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @PathVariable int itemId
    ) {
        log.info("Вызван метод findItemById() в ItemController");
        return itemClient.findItemById(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> add(
            @Validated({Create.class}) @RequestBody ItemDto itemDto,
            @RequestHeader(X_SHARER_USER_ID) int userId
    ) {
        log.info("Вызван метод addItem() в ItemController");
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @Validated({Update.class}) @RequestBody ItemDto itemDto,
            @RequestHeader(X_SHARER_USER_ID) int userId, @PathVariable int itemId
    ) {
        log.info("Вызван метод updateItem() в ItemController");
        return itemClient.updateItem(itemDto, userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @Validated @RequestBody ItemDto itemDto,
            @RequestHeader(X_SHARER_USER_ID) int userId
    ) {
        log.info("Вызван метод deleteItem() в ItemController");
        itemClient.deleteItem(itemDto, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(
            @RequestParam String text,
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Вызван метод search() в ItemController");
        if (text.isBlank()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        } else {
            return itemClient.search(text, userId, from, size);
        }
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @Validated({Create.class}) @RequestBody CommentDto commentDto,
            @PathVariable int itemId
    ) {
        log.info("Вызван метод addComment в ItemController");
        return itemClient.addComment(commentDto, userId, itemId);
    }
}
