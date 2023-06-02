package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class Item {
    private Long id;
    @NotBlank(message = "Поле имени не должно быть пустым")
    private String name;
    @NotBlank(message = "Поле описания не должно быть пустым")
    private String description;
    @NotNull(message = "Поле статуса не должно быть null")
    private Boolean available;
    private Long owner;
    private Long request;
}
