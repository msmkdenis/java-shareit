package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(
            @RequestHeader(HEADER_USER_ID) int userId,
            @Validated({Create.class}) @RequestBody ItemRequestDto requestDto
    ) {
        log.info("Вызван метод addItemRequest() в ItemRequestController");
        return itemRequestClient.addItemRequest(userId, requestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getByOwner(@RequestHeader(HEADER_USER_ID) int userId) {
        log.info("Вызван метод findItemRequestByOwner() в ItemRequestController");
        return itemRequestClient.findItemRequestByOwner(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader(HEADER_USER_ID) int userId,
            @PathVariable int requestId
    ) {
        log.info("Вызван метод getRequestById() в ItemRequestController");
        return itemRequestClient.findRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest(
            @RequestHeader(HEADER_USER_ID) int userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return itemRequestClient.findAllRequests(userId, from, size);
    }
}
