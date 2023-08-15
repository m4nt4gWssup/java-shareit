package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Поле имени не должно быть пустым")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Поле описания не должно быть пустым")
    private String description;
    @NotNull(groups = {Create.class}, message = "Поле статуса не должно быть null")
    private Boolean available;
    @JsonIgnore
    private User owner;
    private Long requestId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}
