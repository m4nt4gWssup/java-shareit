package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto(1L, "Test Comment", null, "Author", LocalDateTime.now());

        User user = Mockito.mock(User.class);
        when(user.getName()).thenReturn("Test User");

        comment = Mockito.mock(Comment.class);
        when(comment.getId()).thenReturn(1L);
        when(comment.getText()).thenReturn("Test Comment Text");
        when(comment.getItem()).thenReturn(null); // Assuming this can be null for the sake of the example.
        when(comment.getAuthor()).thenReturn(user);
        when(comment.getCreated()).thenReturn(LocalDateTime.of(2023, 8, 14, 12, 0));
    }

    @Test
    void serializeCommentDtoToJson() throws Exception {
        assertThat(json.write(commentDto)).extractingJsonPathStringValue("$.text").isEqualTo("Test Comment");
        assertThat(json.write(commentDto)).extractingJsonPathStringValue("$.authorName").isEqualTo("Author");
    }

    @Test
    void testCommentMapper() {
        CommentDto mappedDto = CommentMapper.toCommentDto(comment);

        assertThat(mappedDto.getId()).isEqualTo(comment.getId());
        assertThat(mappedDto.getText()).isEqualTo(comment.getText());
        assertThat(mappedDto.getItem()).isEqualTo(comment.getItem());
        assertThat(mappedDto.getAuthorName()).isEqualTo(comment.getAuthor().getName());
        assertThat(mappedDto.getCreated()).isEqualTo(comment.getCreated());
    }
}
