package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.checker.Checker;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Checker checker;

    @Autowired
    @Lazy
    public UserServiceImpl(UserRepository userRepository, Checker checker) {
        this.userRepository = userRepository;
        this.checker = checker;
    }

    @Override
    public UserDto create(UserDto userDto) {
        try {
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistsException("Пользователь с E-mail=" +
                    userDto.getEmail() + " уже существует");
        }
    }

    @Override
    public UserDto update(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID=" + id + " не найден"));
        if (checker.isValidName(userDto.getName())) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(userDto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(userDto.getId())) {
                    throw new EntityAlreadyExistsException("Пользователь с E-mail=" + userDto.getEmail() + " уже существует");
                }
            });
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void delete(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Пользователь с ID=" + userId + " не найден");
        }
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID=" + userId + " не найден!")));
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID=" + userId + " не найден"));
    }
}
