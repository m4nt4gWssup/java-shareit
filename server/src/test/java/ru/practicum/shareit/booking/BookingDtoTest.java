package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(5), null, 1L, 2L, "ItemName");
    }

    @Test
    void serializeBookingDtoToJson() throws Exception {
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.itemName").isEqualTo("ItemName");
    }
}
