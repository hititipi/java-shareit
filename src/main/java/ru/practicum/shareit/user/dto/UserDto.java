package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validation.marker.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {

    private int id;
    @NotNull(groups = {Create.class})
    private final String name;
    @Email(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private final String email;

}
