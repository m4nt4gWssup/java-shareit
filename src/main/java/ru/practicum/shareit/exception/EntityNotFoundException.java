package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityNotFoundException extends IllegalArgumentException {

    public EntityNotFoundException(String message) {
        log.error(message);
    }
}
