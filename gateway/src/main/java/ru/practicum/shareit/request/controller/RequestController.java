package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.RequestDTO;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequiredArgsConstructor
@RequestMapping("/requests")
@Slf4j
@Validated
public class RequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader(USER_ID) long userId,
                                              @RequestBody RequestDTO request) {
        log.info("Post request: {}", request);
        return client.createRequest(userId, request);
    }

    @GetMapping
    public ResponseEntity<Object> getRequest(@RequestHeader(USER_ID) long userId) {
        log.info("Get request for user: {}", userId);
        return client.getRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest(@RequestHeader(USER_ID) long userId,
                                                @PositiveOrZero @RequestParam(value = "from",
                                                        defaultValue = "0") int from,
                                                @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Get all request for user: {}", userId);
        return client.getAllRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable long requestId,
                                                 @RequestHeader(USER_ID) long userId) {
        log.info("Get request: {}", requestId);
        return client.getRequestById(requestId, userId);
    }
}