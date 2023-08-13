package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long owner);

    ItemDto update(ItemDto itemDto, Long owner, Long itemId);

    void delete(Long itemId, Long owner);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getItemsByOwner(Long owner, Integer from, Integer size);

    List<ItemDto> getItemsBySearchQuery(String text, Integer from, Integer size);

    CommentDto createComment(CommentDto commentDto, Long owner, Long itemId);

    Item findById(Long itemId);
}
