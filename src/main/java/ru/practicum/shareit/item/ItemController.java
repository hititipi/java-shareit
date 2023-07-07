package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.Messages;
import ru.practicum.shareit.validation.marker.Create;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                       @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.addItem());
        Item item = itemService.addItem(ItemMapper.toItem(itemDto, userId));
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("{id}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable("id") int itemId,
                          @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.updateItem(itemDto.getId()));
        Item item = itemService.updateItem(ItemMapper.toItem(itemDto, userId, itemId));
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public Collection<ResponseItemDto> getAll(@RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.getAllItems(userId));
        return itemService.getAll(userId);
    }

    @GetMapping("{id}")
    public ResponseItemDto get(@PathVariable("id") int itemId, @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.getItem(itemId));
        return itemService.getItemForUser(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItemsByText(@RequestParam String text) {
        log.info(Messages.findItems(text));
        return ItemMapper.toItemDto(itemService.findItemsByText(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto addComment(@Valid @RequestBody CommentDto commentDto,
                                         @PathVariable int itemId,
                                         @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.addComment(itemId, userId));
        return itemService.createComment(commentDto, itemId, userId);
    }

}
