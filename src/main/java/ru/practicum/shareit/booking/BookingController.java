package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String OWNER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingFullDto postRequest(@RequestHeader(OWNER) Long userId,
                                      @RequestBody BookingDto bookingDto) {
        log.info("Получен POST-запрос /bookings на создание бронирования пользователем с ID={}", userId);
        return bookingService.postRequest(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingFullDto postApproveBooking(@PathVariable Long bookingId, @RequestHeader(OWNER) Long userId,
                                             @RequestParam Boolean approved) {
        log.info("Получен PATCH-запрос /bookings/{} на одобрение бронирования пользователем с ID={}", bookingId, userId);
        return bookingService.postApproveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingFullDto getBookingReqeust(@PathVariable Long bookingId,
                                            @RequestHeader(OWNER) Long userId) {
        log.info("Получен GET-запрос /bookings/{} для получения информации о бронировании пользователем с ID={}", bookingId, userId);
        return bookingService.getBookingRequest(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingFullDto> getAllBookingRequestForUser(@RequestHeader(OWNER) Long userId,
                                                            @RequestParam(defaultValue = "ALL") String state,
                                                            @RequestParam(value = "from", defaultValue = "0") int from,
                                                            @RequestParam(value = "size", defaultValue = "10")
                                                            int size) {
        log.info("Получен GET-запрос /bookings для получения всех заявок на бронирование пользователем с ID={}", userId);
        return bookingService.getAllBookingRequestForUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingFullDto> getAllBookingRequestForOwner(@RequestHeader(OWNER) Long userId,
                                                             @RequestParam(defaultValue = "ALL") String state,
                                                             @RequestParam(value = "from", defaultValue = "0") int from,
                                                             @RequestParam(value = "size", defaultValue = "10")
                                                             int size) {
        log.info("Получен GET-запрос /bookings/owner для получения всех заявок на бронирование, созданных пользователем с ID={}", userId);
        return bookingService.getAllBookingRequestForOwner(userId, state, from, size);
    }
}
