package ru.practicum.shareit.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    @Transactional
    ItemRequestResponseDto addItemRequest(int userId, ItemRequestDto itemRequestDto);

    @Transactional(readOnly = true)
    List<ItemRequestResponseDto> findItemRequestByOwner(int userId);

    @Transactional(readOnly = true)
    ItemRequestResponseDto findRequestById(int userId, int requestId);

    List<ItemRequestResponseDto> findAllRequests(int userId, int from, int size);
}