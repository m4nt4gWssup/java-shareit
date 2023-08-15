package ru.practicum.shareit.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    @InjectMocks
    ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void notFoundHandler_BookingNotFoundException_returnsErrorResponse() {
        BookingNotFoundException exception = new BookingNotFoundException("Booking not found");
        ErrorResponse actualResponse = errorHandler.notFoundHandler(exception);
        assertEquals("Booking not found", actualResponse.getError());
    }

    @Test
    void badRequestHandle_ValidationException_returnsErrorResponse() {
        ValidationException exception = new ValidationException("Validation failed");
        ErrorResponse actualResponse = errorHandler.badRequestHandle(exception);
        assertEquals("Validation failed", actualResponse.getError());
    }

    @Test
    void handleEntityAlreadyExistsException_EntityAlreadyExistsException_returnsErrorResponse() {
        EntityAlreadyExistsException exception = new EntityAlreadyExistsException("Entity already exists");
        ErrorResponse actualResponse = errorHandler.handleEntityAlreadyExistsException(exception);
        assertEquals("Entity already exists", actualResponse.getError());
    }

    @Test
    void unsupportedStatusHandler_UnsupportedStatusException_returnsErrorResponse() {
        UnsupportedStatusException exception = new UnsupportedStatusException("Unsupported status");
        ErrorResponse actualResponse = errorHandler.unsupportedStatusHandler(exception);
        assertEquals("Unsupported status", actualResponse.getError());
    }
}
