package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.utils.Messages;
import ru.practicum.shareit.validation.marker.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.utils.Constants.DEFAULT_FROM_VALUE;
import static ru.practicum.shareit.utils.Constants.DEFAULT_SIZE_VALUE;
import static ru.practicum.shareit.utils.Constants.USER_ID_HEADER;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@Validated({Create.class}) @RequestBody PostItemRequestDto requestDto,
                                      @RequestHeader(USER_ID_HEADER) int requestorId) {
        log.info(Messages.addItemRequest(requestorId));
        return itemRequestClient.create(requestDto, requestorId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(USER_ID_HEADER) int requestorId) {
        log.info(Messages.getItemRequestsForOwner(requestorId));
        return itemRequestClient.getForOwner(requestorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                         @PositiveOrZero int from,
                                         @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                         @Positive int size,
                                         @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.getAllRequestForUser(userId));
        return itemRequestClient.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable int requestId,
                                          @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.getRequestById(requestId, userId));
        return itemRequestClient.getById(requestId, userId);
    }

}
