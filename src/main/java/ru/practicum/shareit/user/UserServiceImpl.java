package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userDao.create(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        return UserMapper.toUserDto(userDao.update(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        return UserMapper.toUserDto(userDao.delete(userId));
    }

    @Override
    public List<UserDto> getUsers() {
        return userDao.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        return UserMapper.toUserDto(userDao.getUserById(userId));
    }
}
