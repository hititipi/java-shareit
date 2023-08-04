package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserTest() {
        User user = new User(1, "user1", "user1@mail.com");
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(user));
        User gottenUser = userService.getUser(1);
        assertNotNull(gottenUser);
        assertEquals(gottenUser, user);
        verify(userRepository, times(1)).findById(any(Integer.class));
    }

    @Test
    void getAllUsersTest() {
        User user1 = new User(1, "user1", "user1@mail.com");
        User user2 = new User(2, "user2", "user2@mail.com");
        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));
        Collection<User> users = userService.getAllUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void createUserTest() {
        User user = new User(1, "user1", "user1@mail.com");
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        User createdUser = userService.createUser(user);
        assertNotNull(createdUser);
        assertEquals(user, createdUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserTest() {
        User user = new User(1, "user1", null);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        when(userRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(user));
        User updatedUser = userService.updateUser(user);
        assertNotNull(updatedUser);
        assertEquals(updatedUser, user);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUserByIdTest() {
        userService.deleteUser(1);
        verify(userRepository, times(1)).deleteById(1);
    }

}
