package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.checker.Checker;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
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

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        try {
            log.info("Создание пользователя с E-mail={}", userDto.getEmail());
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка при создании пользователя с E-mail={}", userDto.getEmail(), e);
            throw new EntityAlreadyExistsException(
                    String.format("Пользователь с E-mail=%s уже существует", userDto.getEmail()));
        }
    }

    @Transactional
    @Override
    public UserDto update(UserDto userDto, Long id) {
        if (userDto.getId() == null) {
            userDto.setId(id);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID={} не найден", id);
                    return new UserNotFoundException(String.format("Пользователь с ID=%d не найден", id));
                });
        if (checker.isValidName(userDto.getName())) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(userDto.getEmail()).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(userDto.getId())) {
                    throw new EntityAlreadyExistsException(
                            String.format("Пользователь с E-mail=%s уже существует", userDto.getEmail()));
                }
            });
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Ошибка при удалении пользователя с ID={}", userId, e);
            throw new UserNotFoundException(String.format("Пользователь с ID=%d не найден", userId));
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
                .orElseThrow(() -> {
                    log.error("Пользователь с ID={} не найден!", userId);
                    return new UserNotFoundException(String.format("Пользователь с ID=%d не найден!", userId));
                }));
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID={} не найден", userId);
                    return new UserNotFoundException(String.format("Пользователь с ID=%d не найден", userId));
                });
    }
}
