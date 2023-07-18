package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class ItemRequestDto {

    int id;
    String name;
    String description;
    boolean available;
    int requestId;
}