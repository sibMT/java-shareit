package ru.practicum.shareit.comment;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class CommentClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public CommentClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, Object body) {
        return post(API_PREFIX + "/{id}/comment", userId, Map.of("id", itemId), body);
    }
}
