package ru.practicum.shareit.item;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Long ownerId, Object body) {
        return post(API_PREFIX, ownerId, body);
    }

    public ResponseEntity<Object> update(Long ownerId, Long itemId, Object body) {
        return patch(API_PREFIX + "/{id}", ownerId, Map.of("id", itemId), body);
    }

    public ResponseEntity<Object> getById(Long requesterId, Long itemId) {
        return get(API_PREFIX + "/{id}", requesterId, Map.of("id", itemId));
    }

    public ResponseEntity<Object> getByOwner(Long ownerId) {
        return get(API_PREFIX, ownerId);
    }

    public ResponseEntity<Object> search(String text) {
        return get(API_PREFIX + "/search?text={text}", null, Map.of("text", text));
    }
}
