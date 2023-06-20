package ru.practicum.shareit.user.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ValidationErrors;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage {

    private HashMap<Integer, User> users = new HashMap<>();
    private Set<String> emails = new HashSet<>();

    private int id = 0;

    public void checkContainsUserId(int id){
        if (!users.containsKey(id)){
            throw new ValidationException(HttpStatus.NOT_FOUND, ValidationErrors.RESOURCE_NOT_FOUND);
        }
    }

    private void checkContainsEmail(String email){
        if (emails.contains(email)){
            throw new ValidationException(HttpStatus.CONFLICT, ValidationErrors.EMAIL_ALREADY_EXISTS);
        }
    }

    public User getUser(int id) {
        checkContainsUserId(id);
        return users.get(id);
    }


    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User createUser(User user) {
        checkContainsEmail(user.getEmail());
        user.setId(++id);
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(int userId, UserDto userDto) {
        checkContainsUserId(userId);
        User user = users.get(userId);
        String newEmail = userDto.getEmail();
        String oldEmail = user.getEmail();

        if (newEmail != null && !Objects.equals(newEmail, user.getEmail())){
            checkContainsEmail(newEmail);
            emails.remove(user.getEmail());
            user.setEmail(newEmail);
            emails.add(user.getEmail());
        }
        if (userDto.getName() != null){
            user.setName(userDto.getName());;
        }



        return user;
    }

    public void deleteUser(int id) {
        checkContainsUserId(id);
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }
}
