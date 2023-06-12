package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserDao implements UserDao {
    public final Map<Long, User> users;
    private Long newId;

    public InMemoryUserDao() {
        newId = 0L;
        users = new HashMap<>();
    }

    @Override
    public User create(User user) {
        if (users.values().stream().noneMatch(u -> u.getEmail().equals(user.getEmail()))) {
            user.setId(++newId);
            users.put(user.getId(), user);
        } else {
            throw new EntityAlreadyExistsException("Пользователь с E-mail=" + user.getEmail() + " уже существует");
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        if (!users.containsKey(user.getId())) {
            throw new EntityNotFoundException("Пользователь с ID=" + user.getId() + " не найден");
        }
        if (user.getName() == null) {
            user.setName(users.get(user.getId()).getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(users.get(user.getId()).getEmail());
        }
        if (users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .allMatch(u -> u.getId().equals(user.getId()))) {
            users.put(user.getId(), user);
        } else {
            throw new EntityAlreadyExistsException("Пользователь с E-mail=" + user.getEmail() + " уже существует");
        }
        return user;
    }

    @Override
    public User delete(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException("Пользователь с ID=" + userId + " не найден");
        }
        return users.remove(userId);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Long userId) {
        if (userId == null) {
            throw new ValidationException("Передан пустой аргумент");
        }
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException("Пользователь с ID=" + userId + " не найден");
        }
        return users.get(userId);
    }
}
