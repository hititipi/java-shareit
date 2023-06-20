package ru.practicum.shareit.item.storage;

import org.springframework.http.HttpStatus;
import ru.practicum.shareit.exception.ValidationErrors;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public interface ItemStorage {

    public Item addItem(Item item);

    public Item update(Item item);

    public Collection<Item> getAll(int userId);

    public Item get(int itemId);

    public Collection<Item> findItemsByText(String text);
}
