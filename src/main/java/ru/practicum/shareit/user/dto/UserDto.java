package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.interfaces.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Поле имени не должно быть пустым или содержать пробелы")
    private String name;
    @Email(groups = {Create.class, Update.class}, message = "Неправильный формат почты")
    @NotBlank(groups = {Create.class}, message = "Поле имени не должно быть пустым или содержать пробелы")
    private String email;
}
