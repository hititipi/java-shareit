package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest toItemRequest(PostItemRequestDto postItemRequestDto) {
        return ItemRequest.builder()
                .description(postItemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public ResponseItemRequestDto toResponseItemRequestDto(ItemRequest request) {
        return ResponseItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public ResponseItemRequestDto toResponseItemRequestDto(ItemRequest request, List<Item> items) {
        return ResponseItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(ItemMapper.toItemForRequestDto(items))
                .build();
    }
}
