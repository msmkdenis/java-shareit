package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto createItemRequest(
            @RequestHeader(HEADER_USER_ID) int userId,
            @Validated({Create.class}) @RequestBody ItemRequestDto requestDto
    ) {
        log.info("Вызван метод addItemRequest() в ItemRequestController");
        return itemRequestService.addItemRequest(userId, requestDto);
    }

    @GetMapping()
    public List<ItemRequestResponseDto> getByOwner(@RequestHeader(HEADER_USER_ID) int userId) {
        log.info("Вызван метод findItemRequestByOwner() в ItemRequestController");
        return itemRequestService.findItemRequestByOwner(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestResponseDto getRequestById(
            @RequestHeader(HEADER_USER_ID) int userId,
            @PathVariable int requestId
    ) {
        log.info("Вызван метод getRequestById() в ItemRequestController");
        return itemRequestService.findRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequest(
            @RequestHeader(HEADER_USER_ID) int userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return itemRequestService.findAllRequests(userId, from, size);
    }
}
