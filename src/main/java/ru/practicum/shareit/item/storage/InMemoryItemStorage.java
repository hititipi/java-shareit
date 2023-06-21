package ru.practicum.shareit.item.storage;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.validation.ValidationErrors;
import ru.practicum.shareit.validation.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 0;

    private void checkContainsItem(int id) {
        if (!items.containsKey(id)) {
            throw new ValidationException(HttpStatus.NOT_FOUND, ValidationErrors.RESOURCE_NOT_FOUND);
        }
    }

    @Override
    public Item addItem(Item item) {
        item.setId(++id);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        checkContainsItem(item.getId());
        Item oldItem = items.get(item.getId());
        if (oldItem.getOwner() != item.getOwner()) {
            throw new ValidationException(HttpStatus.NOT_FOUND, ValidationErrors.RESOURCE_NOT_FOUND);
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        return oldItem;
    }

    @Override
    public Collection<Item> getAll(int userId) {
        return items.values().stream().filter(item -> item.getOwner() == userId).collect(Collectors.toList());
    }

    @Override
    public Item get(int itemId) {
        checkContainsItem(itemId);
        return items.get(itemId);
    }

    @Override
    public Collection<Item> findItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(lowerText)
                        || item.getDescription().toLowerCase().contains(lowerText))
                .collect(Collectors.toList());
    }
}
