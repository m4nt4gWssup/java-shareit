package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.checker.Checker;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    private final Checker checker;

    @Autowired
    @Lazy
    public ItemMapper(Checker checker) {
        this.checker = checker;
    }

    public ItemDto toItemDto(Item item) {
        if (item == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequestId() != null ? item.getRequestId() : null,
                null,
                null,
                checker.getCommentsByItemId(item.getId())
        );
    }

    public Item toItem(ItemDto itemDto, Long owner) {
        if (itemDto == null || owner == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                checker.getUserById(owner),
                itemDto.getRequest() != null ? itemDto.getRequest() : null
        );
    }
}
