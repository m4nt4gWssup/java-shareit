package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.ItemRequestDTO;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    public static final String USER_ID = "X-Sharer-User-Id";
    private final ItemClient client;

    @GetMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> getItems(@RequestHeader(USER_ID) long ownerId,
                                           @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                           @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Get items for user: {}", ownerId);
        return client.getItems(ownerId, from, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) long userId, @PathVariable long id) {
        log.info("Get item, id: {}", id);
        return client.getItem(userId, id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItemForSearch(@RequestParam(required = false) String text,
                                                   @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                                   @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Search item for: {}", text);
        return client.searchItem(text, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> postItem(@RequestHeader(USER_ID) long ownerId,
                                           @RequestBody @Validated(Create.class) ItemRequestDTO itemDto) {
        log.info("Post item: {}", itemDto);
        return client.createItem(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> postComment(@RequestHeader(USER_ID) long userId, @PathVariable long itemId,
                                              @RequestBody @Validated(Create.class) CommentRequestDTO commentDto) {
        log.info("Post comment: {}", commentDto);
        return client.createComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> patchItem(@RequestHeader(USER_ID) long ownerId, @PathVariable long id,
                                            @Validated(Update.class) @RequestBody ItemRequestDTO itemDto) {
        log.info("Patch item: {}", id);
        return client.updateItem(ownerId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> deleteItem(@RequestHeader(USER_ID) long ownerId, @PathVariable long id) {
        log.info("Delete item: {}", id);
        return client.deleteItem(ownerId, id);
    }
}