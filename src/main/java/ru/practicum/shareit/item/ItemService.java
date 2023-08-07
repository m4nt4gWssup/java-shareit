package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long owner);

    ItemDto update(ItemDto itemDto, Long owner, Long itemId);

    void delete(Long itemId, Long owner);

    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> getItemsByOwner(Long owner);

    List<ItemDto> getItemsBySearchQuery(String text);

    CommentDto createComment(CommentDto commentDto, Long owner, Long itemId);

    List<CommentDto> getCommentsByItemId(Long itemId);
}
