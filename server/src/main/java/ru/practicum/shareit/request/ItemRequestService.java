package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto requestDto, Long userId);

    ItemRequestDto getItemRequestById(Long requestId, Long userId);

    List<ItemRequestDto> getAllRequest(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getRequest(Long userId);
}
