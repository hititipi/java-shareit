package ru.practicum.shareit.item.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PostItemDto {

    private int id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;

}
