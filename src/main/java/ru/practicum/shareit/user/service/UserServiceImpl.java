package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.validation.ValidationErrors;
import ru.practicum.shareit.validation.exception.ValidationException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public User getUser(int id) {
        return userRepository.findById(id).orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND, ValidationErrors.RESOURCE_NOT_FOUND));
    }

    public Collection<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User updatedUser) {
        User user = userRepository.findById(updatedUser.getId()).orElseThrow();
        if (updatedUser.getName() != null) {
            user.setName(updatedUser.getName());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        return user;
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
