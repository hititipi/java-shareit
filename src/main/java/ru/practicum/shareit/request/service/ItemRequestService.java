package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequest createItemRequest(ItemRequest itemRequest, int requestorId);

    List<ResponseItemRequestDto> getForOwner(int requestorId);

    List<ResponseItemRequestDto> getAll(int from, int size, int userId);

    ResponseItemRequestDto getById(int requestId, int userId);

}