package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.PostItemDto;
import ru.practicum.shareit.item.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.Messages;

import java.util.Collection;

import static ru.practicum.shareit.utils.Constants.*;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public PostItemDto add(@RequestBody PostItemDto postItemDto,
                           @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.addItem());
        Item item = itemService.addItem(postItemDto, userId);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("{id}")
    public PostItemDto update(@RequestBody PostItemDto postItemDto,
                              @PathVariable("id") int itemId,
                              @RequestHeader(USER_ID_HEADER) int ownerId) {
        log.info(Messages.updateItem(postItemDto.getId()));
        Item item = itemService.updateItem(ItemMapper.toItem(postItemDto, itemId), ownerId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public Collection<ResponseItemDto> getAll(@RequestHeader(USER_ID_HEADER) int userId,
                                              @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                              int from,
                                              @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                              int size) {
        log.info(Messages.getAllItems(userId));
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("{id}")
    public ResponseItemDto get(@PathVariable("id") int itemId, @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.getItem(itemId));


        System.out.println("get " + itemId + "   " + userId);
        ResponseItemDto result = itemService.getItemForUser(itemId, userId);
        System.out.println("result: " + result);

        return result;
    }

    @GetMapping("/search")
    public Collection<ResponseItemDto> findItemsByText(@RequestParam String text,
                                                       @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                       int from,
                                                       @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                       int size) {
        log.info(Messages.findItems(text));
        return itemService.findItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto addComment(@RequestBody CommentDto commentDto,
                                         @PathVariable int itemId,
                                         @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.addComment(itemId, userId));
        return itemService.createComment(commentDto, itemId, userId);
    }

}
