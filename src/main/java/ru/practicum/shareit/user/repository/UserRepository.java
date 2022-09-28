package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {
    private final List<User> users = new ArrayList<>();
    private int id = 0;

    public List<User> findAll() {
        return users;
    }

    public Optional<User> findById(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findAny();
    }

    public User add(User user) {
        user.setId(calcId());
        users.add(user);
        return user;
    }

    public User update(User newUser, User oldUser) {
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        return oldUser;
    }

    public void delete(User user) {
        users.remove(user);
    }

    public boolean contains(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email))
                return true;
        }
        return false;
    }

    private int calcId() {
        return ++id;
    }
}
