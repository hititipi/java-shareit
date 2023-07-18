package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class ResponseItemRequestDto {

    private int id;
    private String description;
    private List<ItemRequestDto> items = new ArrayList<>();
    private LocalDateTime created;
}
