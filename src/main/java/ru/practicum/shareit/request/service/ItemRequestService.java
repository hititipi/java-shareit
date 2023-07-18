package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequest createItemRequest(ItemRequest itemRequest, int requesterId);

    List<ResponseItemRequestDto> getForOwner(int requesterId);

    List<ResponseItemRequestDto> findAll(int from, int size, int userId);

    ResponseItemRequestDto findById(int requestId, int userId);

}