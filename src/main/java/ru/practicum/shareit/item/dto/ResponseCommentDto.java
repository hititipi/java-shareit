package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@EqualsAndHashCode
@Getter
@ToString
public class ResponseCommentDto {

    private int id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
