package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.exception.ValidationException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import ru.practicum.shareit.validation.ValidationErrors;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationUserServiceTest {

    private final UserService userService;

    @Test
    public void getUserByIdTest() {
        User savedUser = userService.createUser(User.builder().name("user").email("user@mail.com").build());
        User gottenUser = userService.getUser(savedUser.getId());
        assertThat(gottenUser.getId(), notNullValue());
        assertThat(gottenUser.getName(), equalTo(savedUser.getName()));
        assertThat(gottenUser.getEmail(), equalTo(savedUser.getEmail()));
        userService.deleteUser(gottenUser.getId());
    }

    @Test
    public void getUserByInvalidIdTest() {
        userService.createUser(User.builder().name("user").email("user@mail.com").build());
        ValidationException exception = assertThrows(ValidationException.class, () -> userService.getUser(100));
        assertThat(exception.getStatus(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(exception.getMessage(), equalTo(ValidationErrors.RESOURCE_NOT_FOUND));
    }

}

