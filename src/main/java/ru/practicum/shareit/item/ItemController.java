package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.Messages;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public Item add(@Valid @RequestBody ItemDto itemDto,
                        @RequestHeader(USER_ID_HEADER) int userId){
        log.info(Messages.addItem());
        return itemService.addItem(ItemMapper.toItem(itemDto, userId));
    }

    @PatchMapping("{id}")
    public Item update(@RequestBody ItemDto itemDto,
                       @PathVariable("id") int itemId,
                       @RequestHeader(USER_ID_HEADER) int userId){
        log.info(Messages.updateItem(itemDto.getId()));
        return itemService.updateItem(ItemMapper.toItem(itemDto, userId, itemId));
    }

    @GetMapping
    public Collection<ItemDto> getAll(@RequestHeader(USER_ID_HEADER) int userId){
        log.info(Messages.getAllItems(userId));
        return  ItemMapper.toItemDto(itemService.getAll(userId));
    }

    @GetMapping("{id}")
    public ItemDto get(@PathVariable("id") int itemId){
        log.info(Messages.getItem(itemId));
        return ItemMapper.toItemDto(itemService.get(itemId));
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsByText(@RequestParam String text) {
        log.info(Messages.findItems(text));
        return ItemMapper.toItemDto(itemService.findItemsByText(text));
    }

}
