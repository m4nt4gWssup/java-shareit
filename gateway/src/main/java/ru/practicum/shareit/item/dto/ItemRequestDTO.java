package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingRequestDTO;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDTO {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Поле имени не должно быть пустым")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Поле описания не должно быть пустым")
    private String description;
    @NotNull(groups = {Create.class}, message = "Поле статуса не должно быть null")
    private Boolean available;
    private BookingRequestDTO lastBooking;
    private BookingRequestDTO nextBooking;
    private List<CommentRequestDTO> comments;
    private Long requestId;
}