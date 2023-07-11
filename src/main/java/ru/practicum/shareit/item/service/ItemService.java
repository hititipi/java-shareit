package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item addItem(Item item, int userId);

    Item updateItem(Item item, int ownerId);

    ResponseItemDto getItemForUser(int itemId, int userId);

    Collection<ResponseItemDto> getAll(int userId);

    Collection<ResponseItemDto> findItemsByText(String text);

    ResponseCommentDto createComment(CommentDto commentDto, int itemId, int userId);
}
