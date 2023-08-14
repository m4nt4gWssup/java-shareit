package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public class UserMapper {
    public UserDto toUserDto(User user) {
        if (user == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        if (userDto == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }
}
