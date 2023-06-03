package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.interfaces.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "Поле имени не должно быть пустым")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Поле описания не должно быть пустым")
    private String description;
    @NotNull(groups = {Create.class}, message = "Поле статуса не должно быть null")
    private Boolean available;
    private Long request;
}
