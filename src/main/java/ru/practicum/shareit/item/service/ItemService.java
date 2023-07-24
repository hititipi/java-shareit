package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.PostItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item addItem(PostItemDto postItemDto, int userId);

    Item updateItem(Item item, int ownerId);

    ResponseItemDto getItemForUser(int itemId, int userId);

    List<ResponseItemDto> getAll(int userId, int from, int size);

    List<ResponseItemDto> findItemsByText(String text, int from, int size);

    ResponseCommentDto createComment(CommentDto commentDto, int itemId, int userId);
}
