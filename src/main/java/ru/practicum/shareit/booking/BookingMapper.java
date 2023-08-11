package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Data
@AllArgsConstructor
@Component
public class BookingMapper {
    private final ItemMapper itemMapper;

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
                itemMapper.toItemDto(book.getItem()),
                UserMapper.toUserDto(book.getBooker()),
                book.getStatus());
    }
}