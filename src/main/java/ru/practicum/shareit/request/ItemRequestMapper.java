package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestDto toRequestDto(ItemRequest request) {
        return new ItemRequestDto(request.getId(),
                request.getDescription(),
                request.getCreated(),
                request.getItems());
    }
}