package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Integer, Item> itemStorage = new HashMap<>();
    private final Map<Integer, List<Item>> userIndexItemStorage = new HashMap<>();
    private int id = 0;

    @Override
    public Item add(Item item, User owner) {
        item.setOwner(owner);
        final List<Item> itemsList = userIndexItemStorage.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        item.setId(calcId());
        itemStorage.put(item.getId(), item);
        itemsList.add(item);
        userIndexItemStorage.put(owner.getId(), itemsList);
        return item;
    }

    @Override
    public List<Item> findAll(User owner) {
        return userIndexItemStorage.getOrDefault(owner.getId(), Collections.emptyList());
    }

    @Override
    public Optional<Item> findById(int id) {
        if (itemStorage.containsKey(id)) {
            return Optional.of(itemStorage.get(id));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Item update(Item newItem, Item oldItem) {
        if (newItem.getName() != null) {
            oldItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            oldItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            oldItem.setAvailable(newItem.getAvailable());
        }
        return oldItem;
    }

    @Override
    public void delete(Item item) {
        itemStorage.remove(item.getId());
    }

    @Override
    public List<Item> search(String text) {
        String str = text.toLowerCase();
        List<Item> items = new ArrayList<>(itemStorage.values());
        Set<Item> itemListByName = items.stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getName().toLowerCase().contains(str))
                .collect(Collectors.toSet());

        Set<Item> itemListByDescription = items.stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getDescription().toLowerCase().contains(str))
                .collect(Collectors.toSet());
        itemListByName.addAll(itemListByDescription);
        return List.copyOf(itemListByName);
    }

    private int calcId() {
        return ++id;
    }
}
