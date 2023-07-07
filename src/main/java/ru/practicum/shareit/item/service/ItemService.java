package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItem(int itemId);

    ResponseItemDto getItemForUser(int itemId, int userId);

    Collection<ResponseItemDto> getAll(int userId);

    Collection<Item> findItemsByText(String text);

    ResponseCommentDto createComment(CommentDto commentDto, int itemId, int userId);
}
