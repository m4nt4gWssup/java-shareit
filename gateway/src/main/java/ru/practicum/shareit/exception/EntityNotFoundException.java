package ru.practicum.shareit.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String mess) {
        super(mess);
    }
}