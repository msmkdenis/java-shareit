package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.dto.ItemRequestMapper;
import ru.practicum.shareit.dto.ItemRequestResponseDto;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestResponseDto addItemRequest(int userId, ItemRequestDto itemRequestDto) {
        User requester = checkUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requester);
        log.info("Запрос (request) ItemRequest с id = {} сохранен (addItemRequest())", itemRequest.getId());
        return ItemRequestMapper.toItemRequestResponseDto(itemRequestRepository.save(itemRequest), new ArrayList<>());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestResponseDto> findItemRequestByOwner(int userId) {
        checkUser(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        log.info("Получены все запросы (request) пользователя c id = {} (findItemRequestByOwner())", userId);
        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestResponseDto(request,
                        itemRepository.findAllByRequesterId(request.getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestResponseDto findRequestById(int userId, int requestId) {
        checkUser(userId);
        ItemRequest itemRequest = checkItemRequest(requestId);
        log.info("Найден запрос (request) с id = {} (findRequestById())", itemRequest.getId());
        return ItemRequestMapper.toItemRequestResponseDto(itemRequest,
                itemRepository.findAllByRequesterId(itemRequest.getId()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestResponseDto> findAllRequests(int userId, int from, int size) {
        checkUser(userId);
        int page = from / size;
        List<ItemRequest> requests = itemRequestRepository.findAllByOtherUsers(userId, PageRequest.of(page, size));
        log.info("Получены все запросы (request) пользователя c id = {} (findAllRequests())", userId);
        return requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestResponseDto(request,
                        itemRepository.findAllByRequesterId(request.getId())))
                .collect(Collectors.toList());
    }

    private User checkUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ru.practicum.shareit.exception.EntityNotFoundException(String.format("Ошибка! " +
                        "Пользователь с id = %s не найден", id)));
    }

    private ItemRequest checkItemRequest(int id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Ошибка! " +
                        "Запрос(request) с id = %s не найден", id)));
    }
}