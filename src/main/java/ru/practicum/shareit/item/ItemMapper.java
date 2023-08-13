package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;


@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        if (item == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequestId(),
                null,
                null,
                null
        );
    }
}
