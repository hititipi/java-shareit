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

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.shareit.item.ItemController.USER_ID_HEADER;
import static ru.practicum.shareit.utils.Constants.DEFAULT_FROM_VALUE;
import static ru.practicum.shareit.utils.Constants.DEFAULT_SIZE_VALUE;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseItemRequestDto add(@Validated({Create.class}) @RequestBody PostItemRequestDto requestDto,
                                      @RequestHeader("X-Sharer-User-Id") int requesterId) {
        log.info(Messages.addItemRequest(requesterId));
        ItemRequest request = ItemRequestMapper.toItemRequest(requestDto);
        request = itemRequestService.createItemRequest(request, requesterId);
        return ItemRequestMapper.toResponseItemRequestDto(request);
    }

    @GetMapping
    public List<ResponseItemRequestDto> getAllByOwner(@RequestHeader(USER_ID_HEADER) int requesterId) {
        log.info(Messages.getItemRequestsForOwner(requesterId));
        return itemRequestService.getForOwner(requesterId);
    }

    @GetMapping("/all")
    public List<ResponseItemRequestDto> getAll(@RequestParam(defaultValue = DEFAULT_FROM_VALUE)
                                               @Min(0) int from,
                                               @RequestParam(defaultValue = DEFAULT_SIZE_VALUE)
                                               @Positive int size,
                                               @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.getAllRequestForUser(userId));
        return itemRequestService.findAll(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseItemRequestDto getById(@PathVariable int requestId,
                                          @RequestHeader(USER_ID_HEADER) int userId) {
        log.info(Messages.getRequestById(requestId, userId));
        return itemRequestService.findById(requestId, userId);
    }

}
