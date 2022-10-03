package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    public List<ItemDto> findAll(int userId) {
        User owner = UserMapper.toUserDto(userService.findById(userId));
        return itemStorage.findAll(owner).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public ItemDto findById(int id) {
        Item item = itemStorage.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с id = %s не найдена", id)));
        return ItemMapper.toItemDto(item);
    }

    public ItemDto add(ItemDto itemDto, int userId) {
        User owner = UserMapper.toUserDto(userService.findById(userId));
        Item item = itemStorage.add(ItemMapper.toItem(itemDto), owner);
        return ItemMapper.toItemDto(item);
    }

    public ItemDto update(ItemDto itemDto, int userId, int itemId) {
        User owner = UserMapper.toUserDto(userService.findById(userId));
        Item oldItem = itemStorage.findById(itemId).get();
        if (oldItem.getOwner().equals(owner)) {
            Item newItem = itemStorage.update(ItemMapper.toItem(itemDto), oldItem);
            return ItemMapper.toItemDto(newItem);
        }
        throw new EntityNotFoundException("Редактировать информацию о вещи может только ее владелец");
    }

    public void delete(int id) {
        ItemDto item = findById(id);
        itemStorage.delete(ItemMapper.toItem(item));
    }

    public List<ItemDto> search(String text, int userId) {
        userService.findById(userId);
        if (!text.isBlank()) {
            return itemStorage.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
