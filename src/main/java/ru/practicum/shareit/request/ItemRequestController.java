package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto postRequest(@RequestBody ItemRequestDto request,
                                      @RequestHeader(USER_ID) Long userId) {
        log.info("Получен POST-запрос /requests на создание запроса от пользователя с ID={}", userId);
        return service.create(request, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getRequest(@RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос /requests на получение запроса от пользователя с ID={}", userId);
        return service.getRequest(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllRequest(
            @RequestHeader(USER_ID) @Positive Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @PositiveOrZero Integer size) {
        log.info("Получен GET-запрос /requests/all на получение всех запросов " +
                "от пользователя с ID={} с пагинацией from={}, size={}", userId, from, size);
        return service.getAllRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getRequestById(@PathVariable Long requestId,
                                         @RequestHeader(USER_ID) Long userId) {
        log.info("Получен GET-запрос /requests/{} " +
                "на получение запроса с ID={} для пользователя с ID={}", requestId, requestId, userId);
        return service.getItemRequestById(requestId, userId);
    }
}
