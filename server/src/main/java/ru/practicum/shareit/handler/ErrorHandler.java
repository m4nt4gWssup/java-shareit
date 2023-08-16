package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({BookingNotFoundException.class,
            ItemNotFoundException.class,
            UserNotFoundException.class,
            ItemRequestNotFoundException.class,
            GettingNotAvailableException.class,
            UpdateNotAvailableException.class,
            BookingByOwnerNotAvailableException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundHandler(final RuntimeException e) {
        log.info("404: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({ValidationException.class,
            BookingNotAvailableException.class,
            StatusChangingNotAvailableException.class,})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequestHandle(final RuntimeException e) {
        log.info("400: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({EntityAlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEntityAlreadyExistsException(final RuntimeException e) {
        log.info("409: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UnsupportedStatusException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse unsupportedStatusHandler(final RuntimeException e) {
        log.info("500: {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }
}
