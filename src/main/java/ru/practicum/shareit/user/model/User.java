package ru.practicum.shareit.user.model;

import lombok.*;

@Getter
@Setter
@Builder
public class User {

    private Integer id;
    private String name;
    private String email;
}
