package ru.practicum.shareit.exception;

public class UpdateNotAvailableException extends RuntimeException {
    public UpdateNotAvailableException(String message) {
        super(message);
    }
}
