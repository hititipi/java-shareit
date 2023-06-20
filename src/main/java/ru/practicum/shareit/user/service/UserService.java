package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    public User getUser(int id);

    public Collection<User> getAllUsers();

    public User createUser(User user);

    public User updateUser(int userId, UserDto incomeUserDto);


    public void deleteUser(int id);

}
