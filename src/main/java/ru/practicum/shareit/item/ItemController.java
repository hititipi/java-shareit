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
import ru.practicum.shareit.validation.marker.Update;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Collection;
import static ru.practicum.shareit.utils.Constants.DEFAULT_FROM_VALUE;
import static ru.practicum.shareit.utils.Constants.DEFAULT_SIZE_VALUE;


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
        Item item = itemService.addItem(itemDto, userId);
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("{id}")
    public ItemDto update(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @PathVariable("id") int itemId,
                          @RequestHeader(USER_ID_HEADER) int ownerId) {
        log.info(Messages.updateItem(itemDto.getId()));
        Item item = itemService.updateItem(ItemMapper.toItem(itemDto, itemId), ownerId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public Collection<ResponseItemDto> getAll(@RequestHeader(USER_ID_HEADER) int userId,
                                              @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                              @Min(0) int from,
                                              @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                  @Positive int size) {
        log.info(Messages.getAllItems(userId));
        return itemService.getAll(userId, from, size);
    }

    @GetMapping("{id}")
    public ResponseItemDto get(@PathVariable("id") int itemId, @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.getItem(itemId));
        return itemService.getItemForUser(itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ResponseItemDto> findItemsByText(@RequestParam String text,
                                                       @RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                                       @Min(0) int from,
                                                       @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                                           @Positive int size) {
        log.info(Messages.findItems(text));
        return itemService.findItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto addComment(@Valid @RequestBody CommentDto commentDto,
                                         @PathVariable int itemId,
                                         @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.addComment(itemId, userId));
        return itemService.createComment(commentDto, itemId, userId);
    }

}
