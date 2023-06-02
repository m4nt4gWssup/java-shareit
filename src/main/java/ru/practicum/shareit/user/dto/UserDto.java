package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotNull(message = "Поле имени не должно быть null")
    @NotBlank(message = "Поле имени не должно быть пустым или содержать пробелы")
    private String name;
    @NotNull(message = "Поле почты не должно быть null")
    @Email(message = "Неправильный формат почты")
    @NotBlank(message = "Поле имени не должно быть пустым или содержать пробелы")
    private String email;
}
