package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.Messages;
import ru.practicum.shareit.validation.marker.Create;
import ru.practicum.shareit.validation.marker.Update;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping("{id}")
    public UserDto get(@PathVariable int id) {
        log.info(Messages.getUser(id));
        User user = userService.getUser(id);
        return UserMapper.toUserDto(user);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info(Messages.getAllUsers());
        Collection<User> users = userService.getAllUsers();
        return UserMapper.toUserDto(users);
    }

    @PostMapping
    public UserDto add(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info(Messages.addUser());
        User user = userService.createUser(UserMapper.toUser(userDto, null));
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("{id}")
    public UserDto update(@Validated({Update.class}) @RequestBody UserDto userDto,
                          @PathVariable("id") int id) {
        log.info(Messages.updateUser(id));
        User user = UserMapper.toUser(userDto, id);
        return UserMapper.toUserDto(userService.updateUser(user));
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable int id) {
        log.info(Messages.deleteUser(id));
        userService.deleteUser(id);
    }

}
