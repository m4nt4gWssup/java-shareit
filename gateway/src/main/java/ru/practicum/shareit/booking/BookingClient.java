package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.booking.state.BookingStateRequest;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(long userId, BookingRequestDTO booking) {
        return post("", userId, booking);
    }

    public ResponseEntity<Object> postApproveBooking(long bookingId, long userId, boolean approved) {
        return patch("/" + bookingId + "?approved={approved}", userId,
                Map.of("approved", approved), null);
    }

    public ResponseEntity<Object> getBookingRequest(long bookingId, long userId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingRequestForUser(long userId, BookingStateRequest state,
                                                              int from, int size) {
        return get("?state={state}&from={from}&size={size}", userId,
                Map.of("state", state.name(),
                        "from", from,
                        "size", size));
    }

    public ResponseEntity<Object> getAllBookingRequestForOwner(long userId, BookingStateRequest state,
                                                               int from, int size) {
        return get("/owner?state={state}&from={from}&size={size}", userId,
                Map.of("state", state.name(),
                        "from", from,
                        "size", size));
    }
}