package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;

import java.util.List;

public interface BookingService {
    BookingFullDto postRequest(Long userId, BookingDto booking);

    BookingFullDto postApproveBooking(Long bookingId, Long userId, boolean approve);

    BookingFullDto getBookingRequest(Long bookingId, Long userId);

    List<BookingFullDto> getAllBookingRequestForUser(Long userId, String state);

    List<BookingFullDto> getAllBookingRequestForOwner(Long userId, String state);

    Booking getBookingWithUserBookedItem(Long itemId, Long userId);
}
