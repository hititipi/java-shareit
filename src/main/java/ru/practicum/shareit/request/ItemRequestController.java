package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.PostItemRequestDto;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utils.Messages;
import ru.practicum.shareit.validation.marker.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.utils.Constants.DEFAULT_FROM_VALUE;
import static ru.practicum.shareit.utils.Constants.DEFAULT_SIZE_VALUE;
import static ru.practicum.shareit.utils.Constants.USER_ID_HEADER;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseItemRequestDto add(@Validated({Create.class}) @RequestBody PostItemRequestDto requestDto,
                                      @RequestHeader(USER_ID_HEADER) int requestorId) {
        log.info(Messages.addItemRequest(requestorId));
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto);
        request = itemRequestService.createItemRequest(request, requestorId);
        return ItemRequestMapper.toResponseItemRequestDto(request);
    }

    @GetMapping
    public List<ResponseItemRequestDto> getAllByOwner(@RequestHeader(USER_ID_HEADER) int requestorId) {
        log.info(Messages.getItemRequestsForOwner(requestorId));
        return itemRequestService.getForOwner(requestorId);
    }

    @GetMapping("/all")
    public List<ResponseItemRequestDto> getAll(@RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                               @PositiveOrZero int from,
                                               @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                               @Positive int size,
                                               @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.getAllRequestForUser(userId));
        return itemRequestService.getAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseItemRequestDto getById(@PathVariable int requestId,
                                          @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.getRequestById(requestId, userId));
        return itemRequestService.getById(requestId, userId);
    }

}
