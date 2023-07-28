package ru.practicum.shareit.item.dto;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ItemRequestDto {

    private int id;
    private String name;
    private String description;
    private boolean available;
    private int requestId;
}
