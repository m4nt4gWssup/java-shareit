package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.checker.Checker;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final Checker checker;

    @Autowired
    @Lazy
    public ItemServiceImpl(ItemRepository repository, UserRepository userRepository,
                           CommentRepository commentRepository, Checker checker) {
        this.itemRepository = repository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.checker = checker;
    }

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long owner) {
        if (!checker.isExistUser(owner)) {
            log.error("Пользователь с ID={} не найден", owner);
            throw new EntityNotFoundException("Не найдено такого пользователя");
        }
        Item newItem = new Item();
        newItem.setName(itemDto.getName());
        newItem.setDescription(itemDto.getDescription());
        newItem.setAvailable(itemDto.getAvailable());
        newItem.setOwner(userRepository.getById(owner));
        if (itemDto.getRequestId() != null) {
            newItem.setRequestId(itemDto.getRequestId());
        }
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Transactional
    @Override
    public ItemDto update(ItemDto itemDto, Long owner, Long itemId) {
        if (!checker.isExistUser(owner)) {
            log.error("Пользователь с ID={} не найден", owner);
            throw new UserNotFoundException("Не найдено такого пользователя");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Предмет с ID=%d не найден", itemId)));
        if (!item.getOwner().getId().equals(owner)) {
            throw new ItemNotFoundException("У пользователя нет такой вещи");
        }
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        if (checker.isValidString(itemDto.getName())) {
            item.setName(itemDto.getName());
        }
        if (checker.isValidString(itemDto.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(item);
    }

    @Transactional
    @Override
    public void delete(Long itemId, Long owner) {
        if (!checker.isExistUser(owner)) {
            throw new UserNotFoundException("Не найдено такого пользователя");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Предмет с ID=%d не найден", itemId)));
        if (!item.getOwner().getId().equals(owner)) {
            throw new ItemNotFoundException("У пользователя нет такой вещи!");
        }
        itemRepository.deleteById(itemId);
    }

    @Transactional
    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        if (!checker.isExistUser(userId)) {
            log.error("Пользователь с ID={} не найден", userId);
            throw new UserNotFoundException("Не найдено такого пользователя");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с ID=%d не найдена", itemId)));
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.getById(itemId));
        if (checker.getNextBookingByItem(item) != null && userId.equals(item.getOwner().getId())) {
            itemDto.setNextBooking(BookingMapper.toBookingDto(checker.getNextBookingByItem(item)));
        }
        if (checker.getLastBookingByItem(item) != null && userId.equals(item.getOwner().getId())) {
            itemDto.setLastBooking(BookingMapper.toBookingDto(checker.getLastBookingByItem(item)));
        }
        List<CommentDto> commentsByItem = new ArrayList<>();
        if (commentRepository.findAllByItem(item) != null) {
            for (Comment comment : commentRepository.findAllByItem(item)) {
                CommentDto commentDto = CommentMapper.toCommentDto(comment);
                commentsByItem.add(commentDto);
            }
            itemDto.setComments(commentsByItem);
        }
        return itemDto;
    }

    @Transactional
    @Override
    public List<ItemDto> getItemsByOwner(Long owner, Integer from, Integer size) {
        if (!checker.isExistUser(owner)) {
            log.error("Пользователь с ID={} не найден", owner);
            throw new UserNotFoundException("Не найдено такого пользователя");
        }
        ArrayList<ItemDto> items = new ArrayList<>();
        List<CommentDto> commentsByItem = new ArrayList<>();
        for (Item item : itemRepository.findAllByOwnerOrderById(userRepository.getById(owner),
                PageRequest.of(from, size))) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            if (checker.getLastBookingByItem(item) != null) {
                itemDto.setLastBooking(BookingMapper.toBookingDto(checker.getLastBookingByItem(item)));
            }
            if (checker.getNextBookingByItem(item) != null) {
                itemDto.setNextBooking(BookingMapper.toBookingDto(checker.getNextBookingByItem(item)));
            }
            for (Comment comment : commentRepository.findAllByItem(item)) {
                commentsByItem.add(CommentMapper.toCommentDto(comment));
            }
            itemDto.setComments(commentsByItem);
            items.add(itemDto);
        }
        return items;
    }

    @Transactional
    @Override
    public List<ItemDto> getItemsBySearchQuery(String text, Integer from, Integer size) {
        if (text != null && !text.isBlank()) {
            text = text.toLowerCase();
            return itemRepository.getItemsBySearchQuery(text, PageRequest.of(from, size)).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto commentDto, Long owner, Long itemId) {
        if (!checker.isExistUser(owner)) {
            log.error("Пользователь с ID={} не найден", owner);
            throw new UserNotFoundException("Не найдено такого пользователя");
        }
        Comment comment = new Comment();
        Booking booking = checker.getUserBookingForItem(itemId, owner);
        if (booking != null) {
            comment.setCreated(LocalDateTime.now());
            comment.setItem(booking.getItem());
            comment.setAuthor(booking.getBooker());
            comment.setText(commentDto.getText());
        } else {
            throw new ValidationException("Пользователь вещь не бронировал");
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public Item findById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.error("Вещь с ID={} не найдена", itemId);
                    return new ItemNotFoundException(String.format("Вещь с ID=%d не найдена", itemId));
                });
    }
}
