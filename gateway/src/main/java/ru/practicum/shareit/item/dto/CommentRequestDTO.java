package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDTO {
    private Long id;
    @NotNull
    private Long itemId;
    @NotBlank(groups = {Create.class}, message = "Поле имени не должно быть пустым")
    private String text;
    @NotBlank(groups = {Create.class}, message = "Поле имени не должно быть пустым")
    private String authorName;
    @NotNull
    private LocalDateTime created;
}
