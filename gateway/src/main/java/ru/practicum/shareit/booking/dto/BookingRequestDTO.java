package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.state.BookingStateRequest;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
    private Long itemId;
    @NotNull(groups = {Create.class}, message = "Поле статуса не должно быть null")
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull(groups = {Create.class}, message = "Поле статуса не должно быть null")
    @FutureOrPresent
    private LocalDateTime end;
    private BookingStateRequest status;
    private Long bookerId;
    private String itemName;
}
