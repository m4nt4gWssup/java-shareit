package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Long id);

    UserDto delete(Long userId);

    List<UserDto> getUsers();

    UserDto getUserById(Long id);
}
