package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.handler.ErrorResponse;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String OWNER = "X-Sharer-User-Id";

    @PostMapping
    public BookingFullDto create(@RequestHeader(OWNER) Long userId,
                                 @RequestBody BookingDto bookingDto) {
        return bookingService.postRequest(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingFullDto approveBooking(@PathVariable Long bookingId, @RequestHeader(OWNER) Long userId,
                                         @RequestParam Boolean approved) {
        return bookingService.postApproveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingFullDto getBookingReqeust(@PathVariable Long bookingId,
                                            @RequestHeader(OWNER) Long userId) {
        return bookingService.getBookingRequest(bookingId, userId);
    }

    @GetMapping
    public List<BookingFullDto> getAllBookingRequestForUser(@RequestHeader(OWNER) Long userId,
                                                            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingRequestForUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingFullDto> getAllBookingRequestForOwner(@RequestHeader(OWNER) Long userId,
                                                             @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingRequestForOwner(userId, state);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse reqExp(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse nullExp(final NullPointerException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundExp(final EntityNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
