package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.interfaces.Update;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers() {
        log.info("Получен GET-запрос /users на получение списка пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("Получен GET-запрос /users на получение пользователя с ID={}", userId);
        return userService.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Получен POST-запрос /users на добавление пользователя");
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@Validated(Update.class) @RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Получен PATCH-запрос /users на обновление пользователя с ID={}", userId);
        return userService.update(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        log.info("Получен DELETE-запрос /users на удаление пользователя с ID={}", userId);
        userService.delete(userId);
    }
}
