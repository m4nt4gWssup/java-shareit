package ru.practicum.shareit.checker;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class Checker {
    private final UserService userService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    public boolean isExistUser(Long userId) {
        User user = userService.findById(userId);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    public Booking getUserBookingForItem(Long itemId, Long userId) {
        return bookingService.getBookingWithUserBookedItem(itemId, userId);
    }


    public Booking getNextBookingByItem(Item item) {
        return bookingRepository.findFirstByItemAndStatusAndStartAfterOrderByStart(item,
                Status.APPROVED, LocalDateTime.now());
    }

    public Booking getLastBookingByItem(Item item) {
        return bookingRepository.findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(item,
                Status.APPROVED, LocalDateTime.now());
    }

    public boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 255;
    }
}
