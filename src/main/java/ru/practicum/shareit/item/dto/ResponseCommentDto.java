package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ResponseCommentDto {

    private int id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
