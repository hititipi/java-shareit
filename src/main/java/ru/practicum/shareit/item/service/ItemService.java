package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    Item addItem(Item item);

    Item updateItem(Item item);

    Item get(int itemId);

    Collection<Item> getAll(int userId);

    Collection<Item> findItemsByText(String text);

}
