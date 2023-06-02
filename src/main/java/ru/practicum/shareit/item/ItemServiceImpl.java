package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserDao;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto create(ItemDto itemDto, Long owner) {
        if (!isExist(owner)) {
            throw new EntityNotFoundException("Не найдено такого пользователя");
        }
        return ItemMapper.toItemDto(itemDao.create(ItemMapper.toItem(itemDto, owner)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long owner, Long itemId) {
        if (!isExist(owner)) {
            throw new EntityNotFoundException("Не найдено такого пользователя");
        }
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        Item oldItem = itemDao.getItemById(itemId);
        if (!oldItem.getOwner().equals(owner)) {
            throw new EntityNotFoundException("У пользователя нет такой вещи");
        }
        return ItemMapper.toItemDto(itemDao.update(ItemMapper.toItem(itemDto, owner)));
    }

    @Override
    public ItemDto delete(Long itemId, Long owner) {
        Item item = itemDao.getItemById(itemId);
        if (!item.getOwner().equals(owner)) {
            throw new EntityNotFoundException("У пользователя нет такой вещи!");
        }
        return ItemMapper.toItemDto(itemDao.delete(itemId));
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(itemDao.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long owner) {
        return itemDao.getItemsByOwner(owner).stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        text = text.toLowerCase();
        return itemDao.getItemsBySearchQuery(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }

    public boolean isExist(Long userId) {
        boolean exist = false;
        if (userDao.getUserById(userId) != null) {
            exist = true;
        }
        return exist;
    }
}
