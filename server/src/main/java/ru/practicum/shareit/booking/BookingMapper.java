package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@UtilityClass
public class BookingMapper {

    public BookingDto toBookingDto(Booking book) {
        return new BookingDto(book.getId(),
                book.getStart(),
                book.getEnd(),
                book.getStatus(),
                book.getBooker().getId(),
                book.getItem().getId(),
                book.getItem().getName());
    }

    public BookingFullDto toBookingFullDto(Booking book) {
        return new BookingFullDto(book.getId(),
                book.getStart(),
                book.getEnd(),
                ItemMapper.toItemDto(book.getItem()),
                UserMapper.toUserDto(book.getBooker()),
                book.getStatus());
    }
}