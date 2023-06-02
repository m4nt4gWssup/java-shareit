package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
public class InMemoryItemDao implements ItemDao {
    public final Map<Long, Item> items;
    private Long newId;

    public InMemoryItemDao() {
        newId = 0L;
        items = new HashMap<>();
    }

    @Override
    public Item create(Item item) {
        item.setId(++newId);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        if (item.getId() == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        if (!items.containsKey(item.getId())) {
            throw new EntityNotFoundException("Предмет с ID=" + item.getId() + " не найден");
        }
        if (item.getName() == null) {
            item.setName(items.get(item.getId()).getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(items.get(item.getId()).getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(items.get(item.getId()).getAvailable());
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item delete(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        if (!items.containsKey(itemId)) {
            throw new EntityNotFoundException("Предмет с ID=" + itemId + " не найден");
        }
        return items.remove(itemId);
    }

    @Override
    public Item getItemById(Long itemId) {
        if (itemId == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        if (!items.containsKey(itemId)) {
            throw new EntityNotFoundException("Предмет с ID=" + itemId + " не найден");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByOwner(Long ownerId) {
        return new ArrayList<>(items.values().stream()
                .filter(item -> item.getOwner().equals(ownerId))
                .collect(toList()));
    }

    @Override
    public List<Item> getItemsBySearchQuery(String text) {
        List<Item> searchItems = new ArrayList<>();
        if (!text.isBlank()) {
            searchItems = items.values().stream()
                    .filter(item -> item.getAvailable())
                    .filter(item -> item.getName().toLowerCase().contains(text) ||
                            item.getDescription().toLowerCase().contains(text))
                    .collect(toList());
        }
        return searchItems;
    }
}
