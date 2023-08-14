package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        String authorName = (comment.getAuthor() != null) ? comment.getAuthor().getName() : "Аноним";
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem(),
                authorName,
                comment.getCreated());
    }
}
