package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int id = 1;

    public User createUser(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    public User findUser(int userId) {
        return users.get(userId);
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    public void updateUserEmail(int userId, String email) {
        User user = users.get(userId);
        user.setEmail(email);
    }

    public void updateUserName(int userId, String name) {
        User user = users.get(userId);
        user.setName(name);
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void deleteUser(int userId) {
        users.remove(userId);
    }
}
