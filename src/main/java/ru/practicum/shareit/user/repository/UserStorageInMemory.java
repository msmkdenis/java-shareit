package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageInMemory implements UserStorage {
    private final Map<Integer, User> userStorage = new HashMap<>();
    private final Map<String, Integer> userEmailStorage = new HashMap<>();
    private int id = 0;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public Optional<User> findById(int id) {
        if (userStorage.containsKey(id)) {
            return Optional.of(userStorage.get(id));
        }else {
            return Optional.empty();
        }
    }

    @Override
    public User add(User user) {
        user.setId(calcId());
        userStorage.put(user.getId(), user);
        userEmailStorage.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public User update(User newUser, User oldUser) {
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            userEmailStorage.remove(oldUser.getEmail());
            userEmailStorage.put(newUser.getEmail(), newUser.getId());
            oldUser.setEmail(newUser.getEmail());
        }
        return oldUser;
    }

    @Override
    public void delete(User user) {
        userEmailStorage.remove(user.getEmail());
        userStorage.remove(user.getId());
    }

    @Override
    public boolean contains(String email) {
        return userEmailStorage.containsKey(email);

    }

    private int calcId() {
        return ++id;
    }
}
