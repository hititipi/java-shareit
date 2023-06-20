package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final InMemoryUserStorage storage;

    public User getUser(int id) {
        return storage.getUser(id);
    }


    public Collection<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public User createUser(User user) {
        return storage.createUser(user);
    }

    public User updateUser(int userId, UserDto incomeUserDto) {
        return storage.updateUser(userId, incomeUserDto);
    }

    public void deleteUser(int id) {
        storage.deleteUser(id);
    }
}
