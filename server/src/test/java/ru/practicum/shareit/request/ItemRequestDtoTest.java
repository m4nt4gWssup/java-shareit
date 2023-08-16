package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto(1L, "Description", LocalDateTime.now(), null);
    }

    @Test
    void serializeItemRequestDtoToJson() throws Exception {
        assertThat(json.write(itemRequestDto)).extractingJsonPathStringValue("$.description").isEqualTo("Description");
    }
}
