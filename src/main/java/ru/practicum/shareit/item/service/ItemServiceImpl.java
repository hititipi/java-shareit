package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public Item addItem(Item item) {
        userStorage.checkContainsUserId(item.getOwner());
        return itemStorage.addItem(item);
    }

    public Item updateItem(Item item) {
        return itemStorage.update(item);
    }

    public Item get(int itemId) {
        return itemStorage.get(itemId);
    }

    public Collection<Item> getAll(int userId) {
        userStorage.checkContainsUserId(userId);
        return itemStorage.getAll(userId);
    }

    public Collection<Item> findItemsByText(String text) {
        return itemStorage.findItemsByText(text);
    }
}
