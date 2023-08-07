package ru.practicum.shareit.checker;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class Checker {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    public boolean isExistUser(Long userId) {
        boolean exist = false;
        if (userService.getUserById(userId) != null) {
            exist = true;
        }
        return exist;
    }

    public User getUserById(Long userId) {
        return userService.findById(userId);
    }

    public Booking getUserBookingForItem(Long itemId, Long userId) {
        return bookingService.getBookingWithUserBookedItem(itemId, userId);
    }

    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return itemService.getCommentsByItemId(itemId);
    }

    public Booking getNextBookingByItem(Item item) {
        return bookingRepository.findFirstByItemAndStatusAndStartAfterOrderByStart(item,
                Status.APPROVED, LocalDateTime.now());
    }

    public Booking getLastBookingByItem(Item item) {
        return bookingRepository.findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(item,
                Status.APPROVED, LocalDateTime.now());
    }
}
