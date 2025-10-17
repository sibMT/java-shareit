package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> create(Object body) {
        return post(API_PREFIX, body);
    }

    public ResponseEntity<Object> update(Long userId, Object body) {
        return patch(API_PREFIX + "/{id}", null, Map.of("id", userId), body);
    }

    public ResponseEntity<Object> getAll() {
        return get(API_PREFIX);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get(API_PREFIX + "/{id}", null, Map.of("id", userId));
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete(API_PREFIX + "/{id}", null, Map.of("id", userId));
    }
}
