package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long owner);

    ItemDto update(ItemDto itemDto, Long owner, Long itemId);

    ItemDto delete(Long itemId, Long owner);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByOwner(Long owner);

    List<ItemDto> getItemsBySearchQuery(String text);
}
