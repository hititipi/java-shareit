package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.Messages;

import javax.validation.Valid;
import java.util.Collection;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping("{id}")
    public User get(@PathVariable int id){
        log.info(Messages.getUser(id));
        return userService.getUser(id);
    }

    @GetMapping
    public Collection<User> getAll(){
        log.info(Messages.getAllUsers());
        return userService.getAllUsers();
    }

    @PostMapping
    public User add(@Valid @RequestBody User user){
        log.info(Messages.addUser());
        return userService.createUser(user);
    }

    @PatchMapping("{id}")
    public User update(@Valid @RequestBody UserDto userDto,
                       @PathVariable("id") int id){
        log.info(Messages.updateUser(id));
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable int id){
        log.info(Messages.deleteUser(id));
        userService.deleteUser(id);
    }


}
