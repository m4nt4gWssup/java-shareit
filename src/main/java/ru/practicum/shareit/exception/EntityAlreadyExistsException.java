package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityAlreadyExistsException extends IllegalArgumentException {

    public EntityAlreadyExistsException(String message) {
        log.error(message);
    }
}
