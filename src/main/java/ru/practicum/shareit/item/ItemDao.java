package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item create(Item item);

    Item update(Item item);

    Item delete(Long userId);

    Item getItemById(Long itemId);

    List<Item> getItemsByOwner(Long ownerId);

    List<Item> getItemsBySearchQuery(String text);
}
