package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.checker.Checker;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final Checker checker;

    @Autowired
    @Lazy
    public ItemServiceImpl(ItemRepository repository, UserRepository userRepository,
                           CommentRepository commentRepository, ItemMapper itemMapper,
                           BookingMapper bookingMapper, Checker checker) {
        this.itemRepository = repository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
        this.bookingMapper = bookingMapper;
        this.checker = checker;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long owner) {
        if (!checker.isExistUser(owner)) {
            throw new EntityNotFoundException("Не найдено такого пользователя");
        }
        return itemMapper.toItemDto(itemRepository.save(itemMapper.toItem(itemDto, owner)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long owner, Long itemId) {
        if (!checker.isExistUser(owner)) {
            throw new EntityNotFoundException("Не найдено такого пользователя");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Предмет с ID=" + itemId + " не найден"));
        if (!item.getOwner().getId().equals(owner)) {
            throw new EntityNotFoundException("У пользователя нет такой вещи");
        }
        if (itemDto.getId() == null) {
            itemDto.setId(itemId);
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public void delete(Long itemId, Long owner) {
        if (!checker.isExistUser(owner)) {
            throw new EntityNotFoundException("Не найдено такого пользователя");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Предмет с ID=" + itemId + " не найден"));
        if (!item.getOwner().getId().equals(owner)) {
            throw new EntityNotFoundException("У пользователя нет такой вещи!");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public ItemDto getItemById(Long itemId, Long userId) {
        if (!checker.isExistUser(userId)) {
            throw new EntityNotFoundException("Не найдено такого пользователя");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с ID=" + itemId + " не найдена"));
        ItemDto itemDto = itemMapper.toItemDto(itemRepository.getById(itemId));
        if (checker.getNextBookingByItem(item) != null && userId == item.getOwner().getId()) {
            itemDto.setNextBooking(bookingMapper.toBookingDto(checker.getNextBookingByItem(item)));
        }
        if (checker.getLastBookingByItem(item) != null && userId == item.getOwner().getId()) {
            itemDto.setLastBooking(bookingMapper.toBookingDto(checker.getLastBookingByItem(item)));
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

    @Override
    public List<ItemDto> getItemsByOwner(Long owner) {
        if (!checker.isExistUser(owner)) {
            throw new EntityNotFoundException("Не найдено такого пользователя");
        }
        ArrayList<ItemDto> items = new ArrayList<>();
        List<CommentDto> commentsByItem = new ArrayList<>();
        for (Item item : itemRepository.findAllByOwnerOrderById(userRepository.getById(owner))) {
            ItemDto itemDto = itemMapper.toItemDto(item);
            if (checker.getLastBookingByItem(item) != null) {
                itemDto.setLastBooking(bookingMapper.toBookingDto(checker.getLastBookingByItem(item)));
            }
            if (checker.getNextBookingByItem(item) != null) {
                itemDto.setNextBooking(bookingMapper.toBookingDto(checker.getNextBookingByItem(item)));
            }
            for (Comment comment : commentRepository.findAllByItem(item)) {
                commentsByItem.add(CommentMapper.toCommentDto(comment));
            }
            itemDto.setComments(commentsByItem);
            items.add(itemDto);
        }
        return items;
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        if ((text != null) && (!text.isEmpty()) && (!text.isBlank())) {
            text = text.toLowerCase();
            return itemRepository.getItemsBySearchQuery(text).stream()
                    .map(itemMapper::toItemDto)
                    .collect(toList());
        } else return new ArrayList<>();
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long owner, Long itemId) {
        if (!checker.isExistUser(owner)) {
            throw new EntityNotFoundException("Не найдено такого пользователя");
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

    @Override
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(CommentMapper::toCommentDto)
                .collect(toList());
    }
}
