package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto(1L, "Test Comment", null, "Author", LocalDateTime.now());
    }

    @Test
    void serializeCommentDtoToJson() throws Exception {
        assertThat(json.write(commentDto)).extractingJsonPathStringValue("$.text").isEqualTo("Test Comment");
        assertThat(json.write(commentDto)).extractingJsonPathStringValue("$.authorName").isEqualTo("Author");
    }
}
