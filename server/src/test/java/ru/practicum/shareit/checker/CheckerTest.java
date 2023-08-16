package ru.practicum.shareit.checker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckerTest {

    @InjectMocks
    Checker checker;

    @Mock
    UserService userService;

    @Mock
    BookingService bookingService;

    @Mock
    BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void isExistUser_existingUser_returnsTrue() {
        User user = new User();
        user.setId(1L);
        when(userService.findById(1L)).thenReturn(user);

        boolean result = checker.isExistUser(1L);

        assertTrue(result);
    }

    @Test
    void isExistUser_nonExistingUser_returnsFalse() {
        when(userService.findById(1L)).thenReturn(null);
        boolean result = checker.isExistUser(1L);
        assertFalse(result);
    }

    @Test
    void getUserBookingForItem_validInput_returnsBooking() {
        Booking booking = new Booking();
        when(bookingService.getBookingWithUserBookedItem(1L, 1L)).thenReturn(booking);

        Booking result = checker.getUserBookingForItem(1L, 1L);
        assertEquals(booking, result);
    }

    @Test
    void getUserBookingForItem_invalidInput_returnsNull() {
        when(bookingService.getBookingWithUserBookedItem(1L, 1L)).thenReturn(null);

        Booking result = checker.getUserBookingForItem(1L, 1L);
        assertNull(result);
    }

    @Test
    void getNextBookingByItem_validInput_returnsBooking() {
        Item item = new Item();
        Booking booking = new Booking();
        when(bookingRepository.findFirstByItemAndStatusAndStartAfterOrderByStart(any(Item.class), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(booking);

        Booking result = checker.getNextBookingByItem(item);
        assertEquals(booking, result);
    }

    @Test
    void isValidString_validString_returnsTrue() {
        assertTrue(checker.isValidString("test"));
    }

    @Test
    void isValidString_nullString_returnsFalse() {
        assertFalse(checker.isValidString(null));
    }

    @Test
    void isValidString_emptyString_returnsFalse() {
        assertFalse(checker.isValidString(" "));
    }

    @Test
    void isValidName_validName_returnsTrue() {
        assertTrue(checker.isValidName("John"));
    }

    @Test
    void isValidName_longName_returnsFalse() {
        String longName = "a".repeat(256);
        assertFalse(checker.isValidName(longName));
    }

    @Test
    void isValidName_tooLongName_returnsFalse() {
        String longName = "a".repeat(256);
        assertFalse(checker.isValidName(longName));
    }

    @Test
    void getLastBookingByItem_validInput_returnsBooking() {
        Item item = new Item();
        Booking booking = new Booking();
        when(bookingRepository.findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(any(Item.class), eq(Status.APPROVED), any(LocalDateTime.class)))
                .thenReturn(booking);
        Booking result = checker.getLastBookingByItem(item);
        assertEquals(booking, result);
    }
}
