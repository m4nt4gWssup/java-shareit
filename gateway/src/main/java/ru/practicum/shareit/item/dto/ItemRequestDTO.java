package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDTO {
    Long id;
    @NotBlank
    @NonNull
    String name;
    @NonNull
    @NotBlank
    String description;
    Boolean available;
    BookingRequestDTO lastBooking;
    BookingRequestDTO nextBooking;
    List<CommentRequestDTO> comments;
    Integer requestId;
}