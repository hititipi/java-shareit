package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.PostItemDto;
import ru.practicum.shareit.utils.Messages;
import ru.practicum.shareit.validation.marker.Create;
import ru.practicum.shareit.validation.marker.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utils.Constants.*;

@Slf4j
@Validated
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@Validated({Create.class}) @RequestBody PostItemDto postItemDto,
                                      @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.addItem());
        return itemClient.create(postItemDto, userId);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> update(@Validated({Update.class}) @RequestBody PostItemDto postItemDto,
                                         @PathVariable("id") int itemId,
                                         @RequestHeader(USER_ID_HEADER) int ownerId) {
        log.info(Messages.updateItem(postItemDto.getId()));
        return itemClient.update(postItemDto, itemId, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID_HEADER) int userId,
                                         @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                         @PositiveOrZero int from,
                                         @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                         @Positive int size) {
        log.info(Messages.getAllItems(userId));
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> get(@PathVariable("id") int itemId, @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.getItem(itemId));
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByText(@RequestParam String text,
                                                  @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                  @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                  @Positive int size) {
        log.info(Messages.findItems(text));
        return itemClient.findItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto commentDto,
                                             @PathVariable int itemId,
                                             @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.addComment(itemId, userId));
        return itemClient.createComment(commentDto, itemId, userId);
    }

}
