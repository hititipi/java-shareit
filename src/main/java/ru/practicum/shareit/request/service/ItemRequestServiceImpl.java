package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ResponseItemRequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.ShareitPageRequest;
import ru.practicum.shareit.validation.exception.ValidationException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.utils.Sorts.SORT_BY_CREATED_DESC;
import static ru.practicum.shareit.validation.ValidationErrors.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest createItemRequest(ItemRequest itemRequest, int requestorId) {
        User user = findUser(requestorId);
        itemRequest.setRequestor(user);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ResponseItemRequestDto> getForOwner(int requestorId) {
        findUser(requestorId);
        List<ItemRequest> requests = itemRequestRepository.findRequestByRequestorIdOrderByCreatedDesc(requestorId);
        Map<ItemRequest, List<Item>> itemsByRequest = findItemsByRequests(requests);
        return requests.stream()
                .map(request -> ItemRequestMapper.toResponseItemRequestDto(request, itemsByRequest.get(request)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseItemRequestDto> getAll(int from, int size, int userId) {
        findUser(userId);
        Pageable page = new ShareitPageRequest(from, size, SORT_BY_CREATED_DESC);
        List<ItemRequest> requests = itemRequestRepository.findAllForUser(userId, page).toList();
        Map<ItemRequest, List<Item>> itemsByRequest = findItemsByRequests(requests);
        return itemsByRequest.entrySet().stream()
                .map(entry -> ItemRequestMapper.toResponseItemRequestDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseItemRequestDto getById(int requestId, int userId) {
        findUser(userId);
        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND));
        List<Item> items = itemRepository.findAllByItemRequest(request);
        return ItemRequestMapper.toResponseItemRequestDto(request, items);
    }

    private Map<ItemRequest, List<Item>> findItemsByRequests(List<ItemRequest> requests) {
        return itemRepository.findAllByRequestIdIn(requests)
                .stream()
                .collect(Collectors.groupingBy(Item::getItemRequest, Collectors.toList()));
    }

    private User findUser(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ValidationException(HttpStatus.NOT_FOUND, RESOURCE_NOT_FOUND));
    }

}
