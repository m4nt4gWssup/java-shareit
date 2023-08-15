package ru.practicum.shareit.exception;

public class BookingByOwnerNotAvailableException extends RuntimeException {
    public BookingByOwnerNotAvailableException(String message) {
        super(message);
    }
}
