package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Component
@Data
@AllArgsConstructor
public class ItemRequestMapper {

    public static ItemRequestDto toRequestDto(ItemRequest request) {
        return new ItemRequestDto(request.getId(),
                request.getDescription(),
                request.getCreated(),
                request.getItems());
    }
}