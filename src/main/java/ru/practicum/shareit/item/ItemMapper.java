package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.PostItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {

    public ResponseItemDto toResponseItemDto(Item item, Booking last, Booking next, List<Comment> comments) {
        return ResponseItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.toBookingReferencedDto(last))
                .nextBooking(BookingMapper.toBookingReferencedDto(next))
                .comments(CommentMapper.toResponseCommentDto(comments))
                .requestId(item.getItemRequest() == null ? null : item.getItemRequest().getId())
                .build();
    }

    public Item toItem(PostItemDto postItemDto, int itemId) {
        return Item.builder()
                .name(postItemDto.getName())
                .description(postItemDto.getDescription())
                .available(postItemDto.getAvailable())
                .id(itemId)
                .build();
    }

    public Item toItem(PostItemDto postItemDto) {
        return Item.builder()
                .name(postItemDto.getName())
                .description(postItemDto.getDescription())
                .available(postItemDto.getAvailable())
                .build();
    }

    public PostItemDto toItemDto(Item item) {
        return new PostItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getItemRequest() == null ? null : item.getItemRequest().getId());
    }

    public Collection<PostItemDto> toItemDto(Collection<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public ItemRequestDto toItemForRequestDto(Item item) {
        return ItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest().getId())
                .build();
    }

    public List<ItemRequestDto> toItemForRequestDto(List<Item> items) {
        if (items == null) {
            return Collections.EMPTY_LIST;
        }
        return items.stream().map(ItemMapper::toItemForRequestDto).collect(Collectors.toList());
    }

}
