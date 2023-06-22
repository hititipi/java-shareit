package ru.practicum.shareit.user.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.validation.ValidationErrors;
import ru.practicum.shareit.validation.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    private int id = 0;

    public void checkContainsUserId(int id) {
        if (!users.containsKey(id)) {
            throw new ValidationException(HttpStatus.NOT_FOUND, ValidationErrors.RESOURCE_NOT_FOUND);
        }
    }

    private void checkContainsEmail(String email) {
        if (emails.contains(email)) {
            throw new ValidationException(HttpStatus.CONFLICT, ValidationErrors.EMAIL_ALREADY_EXISTS);
        }
    }

    @Override
    public User getUser(int id) {
        checkContainsUserId(id);
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        checkContainsEmail(user.getEmail());
        user.setId(++id);
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User updatedUser) {
        checkContainsUserId(updatedUser.getId());
        User user = users.get(updatedUser.getId());
        String newEmail = updatedUser.getEmail();
        if (newEmail != null && !Objects.equals(newEmail, user.getEmail())) {
            checkContainsEmail(newEmail);
            emails.remove(user.getEmail());
            user.setEmail(newEmail);
            emails.add(user.getEmail());
        }
        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        return user;
    }

    @Override
    public void deleteUser(int id) {
        checkContainsUserId(id);
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }
}
