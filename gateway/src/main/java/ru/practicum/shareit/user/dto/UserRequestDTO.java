package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Поле имени не должно быть пустым или содержать пробелы")
    private String name;
    @Email(groups = {Create.class, Update.class}, message = "Неправильный формат почты")
    @NotBlank(groups = {Create.class}, message = "Поле имени не должно быть пустым или содержать пробелы")
    private String email;
}
