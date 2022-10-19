package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemResponseDto> findAll(@RequestHeader(X_SHARER_USER_ID) int userId) {
        log.info("Вызван метод findAll() в ItemController");
        return itemService.findAll(userId);
    }

    @GetMapping("{itemId}")
    public ItemResponseDto findById(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @PathVariable int itemId
    ) {
        log.info("Вызван метод findItemById() в ItemController");
        return itemService.findItemById(itemId, userId);
    }

    @PostMapping
    public ItemDto add(
            @Validated({Create.class}) @RequestBody ItemDto itemDto,
            @RequestHeader(X_SHARER_USER_ID) int userId
    ) {
        log.info("Вызван метод addItem() в ItemController");
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @Validated({Update.class}) @RequestBody ItemDto itemDto,
            @RequestHeader(X_SHARER_USER_ID) int userId, @PathVariable int itemId
    ) {
        log.info("Вызван метод updateItem() в ItemController");
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<HttpStatus> deleteItem(@PathVariable int itemId) {
        log.info("Вызван метод deleteItem() в ItemController");
        itemService.deleteItem(itemId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(
            @RequestParam String text,
            @RequestHeader(X_SHARER_USER_ID) int userId
    ) {
        log.info("Вызван метод search() в ItemController");
        return itemService.search(text, userId);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(
            @RequestHeader(X_SHARER_USER_ID) int userId,
            @Validated({Create.class}) @RequestBody CommentDto commentDto,
            @PathVariable int itemId
    ) {
        log.info("Вызван метод addComment в ItemController");
        return itemService.addComment(commentDto, userId, itemId);
    }
}
