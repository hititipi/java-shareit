package ru.practicum.shareit.item.dto;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemRequestDto {

    int id;
    String name;
    String description;
    boolean available;
    int requestId;
}
