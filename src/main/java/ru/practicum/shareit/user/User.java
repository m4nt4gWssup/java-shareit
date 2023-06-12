package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank(message = "Поле имени не должно быть пустым или содержать пробелы")
    private String name;
    @Email(message = "Неправильный формат почты")
    @NotBlank(message = "Поле имени не должно быть пустым или содержать пробелы")
    private String email;
}
