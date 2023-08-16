package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.state.BookingStateRequest;
import ru.practicum.shareit.exceptions.UnsupportedStatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.item.controller.ItemController.USER_ID;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingClient client;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> postBookingRequest(@RequestHeader(USER_ID) long userId,
                                                     @Validated(Create.class) @RequestBody BookingRequestDTO bookingDto) {
        log.info("Post booking request: {}", bookingDto);
        checkStartAndEndTimes(bookingDto);
        return client.createRequest(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> postApproveBooking(@PathVariable long bookingId,
                                                     @RequestHeader(USER_ID) long userId,
                                                     @RequestParam boolean approved) {
        log.info("Approve booking request: {}, {}", bookingId, approved);
        return client.postApproveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookingReqeust(@NotNull @PathVariable long bookingId,
                                                    @NotNull @RequestHeader(USER_ID) long userId) {
        log.info("Get booking: {}", bookingId);
        return client.getBookingRequest(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllBookingRequestForUser(@RequestHeader(USER_ID) long userId,
                                                              @RequestParam(defaultValue = "ALL") String state,
                                                              @PositiveOrZero @RequestParam(value = "from",
                                                                      defaultValue = "0") int from,
                                                              @Positive @RequestParam(value = "size",
                                                                      defaultValue = "10")
                                                              int size) {
        BookingStateRequest stateRequest = BookingStateRequest.parse(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        log.info("Get booking for user: {}. from: {}; size: {}", userId, from, size);
        return client.getAllBookingRequestForUser(userId, stateRequest, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllBookingRequestForOwner(@RequestHeader(USER_ID) long userId,
                                                               @RequestParam(defaultValue = "ALL") String state,
                                                               @PositiveOrZero @RequestParam(value = "from",
                                                                       defaultValue = "0") int from,
                                                               @Positive @RequestParam(value = "size",
                                                                       defaultValue = "10")
                                                               int size) {
        BookingStateRequest stateRequest = BookingStateRequest.parse(state)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS"));
        log.info("Get booking for owner: {}. from: {}; size: {}", userId, from, size);
        return client.getAllBookingRequestForOwner(userId, stateRequest, from, size);
    }

    private void checkStartAndEndTimes(BookingRequestDTO bookingDto) {
        if (bookingDto.getStart() == null) {
            String message = "Время начала бронирования не может быть равным null.";
            log.info(message);
            throw new ValidationException(message);
        }

        if (bookingDto.getEnd() == null) {
            String message = "Время окончания бронирования не может быть равным null.";
            log.info(message);
            throw new ValidationException(message);
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            String message = "Окончание бронирования не может быть раньше его начала.";
            log.info(message);
            throw new ValidationException(message);
        }
    }
}