package ru.practicum.shareit.request.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private Long id;
    @NonNull
    private String description;
}
