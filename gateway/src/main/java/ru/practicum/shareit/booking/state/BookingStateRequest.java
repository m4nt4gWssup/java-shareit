package ru.practicum.shareit.booking.state;


import java.util.Optional;

public enum BookingStateRequest {
    ALL,
    REJECTED,
    WAITING,
    FUTURE,
    PAST,
    CURRENT;

    public static Optional<BookingStateRequest> parse(String stringState) {
        for (BookingStateRequest state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}