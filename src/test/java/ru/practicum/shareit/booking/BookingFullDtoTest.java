package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingFullDtoTest {
    @Autowired
    private JacksonTester<BookingFullDto> json;
    private BookingFullDto bookingFullDto;

    @BeforeEach
    void setUp() {
        UserDto booker = new UserDto(1L, "Alex", "alex@alex.ru");
        ItemDto item = new ItemDto(1L, "ItemName", "Description", true, null, null, null, null, null);
        bookingFullDto = new BookingFullDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(5), item, booker, null);
    }

    @Test
    void serializeBookingFullDtoToJson() throws Exception {
        assertThat(json.write(bookingFullDto)).extractingJsonPathStringValue("$.item.name").isEqualTo("ItemName");
        assertThat(json.write(bookingFullDto)).extractingJsonPathStringValue("$.booker.name").isEqualTo("Alex");
    }
}
