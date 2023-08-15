package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.ItemRequestDTO;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemClient client;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_ID) long ownerId,
                                           @RequestParam(value = "from", defaultValue = "0") int from,
                                           @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Get items for user: {}", ownerId);
        return client.getItems(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) long userId, @PathVariable long id) {
        log.info("Get item, id: {}", id);
        return client.getItem(userId, id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemForSearch(@RequestParam(required = false) String text,
                                                   @RequestParam(value = "from", defaultValue = "0") int from,
                                                   @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Search item for: {}", text);
        return client.searchItem(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader(USER_ID) long ownerId,
                                           @RequestBody ItemRequestDTO itemDto) {
        log.info("Post item: {}", itemDto);
        return client.createItem(ownerId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(USER_ID) long userId, @PathVariable long itemId,
                                              @RequestBody CommentRequestDTO commentDto) {
        log.info("Post comment: {}", commentDto);
        return client.createComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchItem(@RequestHeader(USER_ID) long ownerId, @PathVariable long id,
                                            @RequestBody ItemRequestDTO itemDto) {
        log.info("Patch item: {}", id);
        return client.updateItem(ownerId, id, itemDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(USER_ID) long ownerId, @PathVariable long id) {
        log.info("Delete item: {}", id);
        return client.deleteItem(ownerId, id);
    }
}