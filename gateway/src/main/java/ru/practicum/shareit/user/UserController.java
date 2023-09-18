package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.Messages;
import ru.practicum.shareit.validation.marker.Create;
import ru.practicum.shareit.validation.marker.Update;

@Slf4j
@RequiredArgsConstructor
@Controller
@Validated
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping("{id}")
    public ResponseEntity<Object> get(@PathVariable int id) {
        log.info(Messages.getUser(id));
        return userClient.getById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info(Messages.getAllUsers());
        return userClient.getAll();
    }

    @PostMapping
    public ResponseEntity<Object> add(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info(Messages.addUser());
        return userClient.create(userDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@Validated({Update.class}) @RequestBody UserDto userDto,
                                         @PathVariable("id") int id) {
        log.info(Messages.updateUser(id));
        return userClient.update(userDto, id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {
        log.info(Messages.deleteUser(id));
        return userClient.deleteById(id);
    }

}
