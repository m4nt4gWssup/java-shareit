package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDTO;
import ru.practicum.shareit.item.dto.ItemRequestDTO;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(long ownerId, long from, int size) {
        return get("?from={from}&size={size}", ownerId, Map.of("from", from,
                "size", size));
    }

    public ResponseEntity<Object> createItem(long ownerId, ItemRequestDTO item) {
        return post("", ownerId, item);
    }

    public ResponseEntity<Object> updateItem(long ownerId, long id, ItemRequestDTO item) {
        return patch("/" + id, ownerId, item);
    }

    public ResponseEntity<Object> deleteItem(long ownerId, long id) {
        return delete("/" + id, ownerId);
    }

    public ResponseEntity<Object> searchItem(String text, int from, int size) {
        return get("/search?text={text}&from={from}&size={size}", null, Map.of(
                "text", text,
                "from", from,
                "size", size
        ));
    }

    public ResponseEntity<Object> createComment(long userId, long itemId, CommentRequestDTO commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}