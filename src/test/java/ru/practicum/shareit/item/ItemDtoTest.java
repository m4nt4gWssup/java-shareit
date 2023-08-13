package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "ItemName", "Description", true, null, null, null, null, null);
    }

    @Test
    void serializeItemDtoToJson() throws Exception {
        assertThat(json.write(itemDto)).extractingJsonPathStringValue("$.name").isEqualTo("ItemName");
        assertThat(json.write(itemDto)).extractingJsonPathStringValue("$.description").isEqualTo("Description");
    }
}
