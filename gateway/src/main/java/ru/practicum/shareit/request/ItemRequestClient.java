package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Long userId, Object body) {
        return post(API_PREFIX, userId, body);
    }

    public ResponseEntity<Object> getOwn(Long userId) {
        return get(API_PREFIX, userId);
    }

    public ResponseEntity<Object> getAll(Long userId, int from, int size) {
        return get(API_PREFIX + "/all?from={from}&size={size}",
                userId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get(API_PREFIX + "/{id}", userId, Map.of("id", requestId));
    }

}
