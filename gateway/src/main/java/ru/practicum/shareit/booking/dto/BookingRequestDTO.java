package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.booking.state.BookingStateRequest;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
    private Long itemId;
    @NonNull
    private LocalDateTime start;
    @NonNull
    private LocalDateTime end;
    private BookingStateRequest status;
    private Long bookerId;
    private String itemName;
}
