package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserService userService;

    public List<Item> findAll(int userId) {
        User owner = userService.findById(userId);
        return itemRepository.findAll(owner);
    }

    public ItemDto findById(int id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id = %s не найдена", id)));
        return itemMapper.toItemDto(item);
    }

    public ItemDto save(ItemDto itemDto, int userId) {
        User owner = userService.findById(userId);
        Item item = itemRepository.save(itemMapper.toItem(itemDto), owner);
        return itemMapper.toItemDto(item);
    }

    public ItemDto edit(ItemDto itemDto, int userId, int itemId) {
        User owner = userService.findById(userId);
        Item oldItem = itemRepository.findById(itemId).get();
        if (oldItem.getOwner() == owner) {
            Item newItem = itemRepository.edit(itemMapper.toItem(itemDto), oldItem);
            return itemMapper.toItemDto(newItem);
        }
        throw new NotFoundException("Редактировать информацию о вещи может только ее владелец");
    }

    public void delete(int id) {
        ItemDto item = findById(id);
        itemRepository.delete(itemMapper.toItem(item));
    }

    public List<Item> search(String text, int userId) {
        userService.findById(userId);
        List<Item> listItem = new ArrayList<>();
        if (text.length() != 0) {
            listItem = itemRepository.search(text);
        }
        return listItem;
    }

}
