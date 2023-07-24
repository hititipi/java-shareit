package ru.practicum.shareit.user.service;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.practicum.shareit.TestUtils.owner;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntegrationUserServiceTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    public void getUserByIdTest() {
        User savedUser = userService.createUser(owner);
        User gottenUser = userService.getUser(savedUser.getId());

        // надо получить пользователя через EntityManger и сним сравнить

        assertThat(gottenUser.getId(), notNullValue());
        assertThat(gottenUser.getName(), equalTo(owner.getName()));
        assertThat(gottenUser.getEmail(), equalTo(owner.getEmail()));
        userService.deleteUser(gottenUser.getId());
    }

}

