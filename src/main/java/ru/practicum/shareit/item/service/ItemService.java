package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {

    public Item addItem(Item item);

    public Item updateItem(Item item);

    public Item get(int itemId);

    public Collection<Item> getAll(int userId);

    public Collection<Item> findItemsByText(String text);

}
