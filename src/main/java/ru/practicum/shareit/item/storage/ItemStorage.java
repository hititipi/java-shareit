package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {

    Item addItem(Item item);

    Item update(Item item);

    Collection<Item> getAll(int userId);

    Item get(int itemId);

    Collection<Item> findItemsByText(String text);
}
