package ru.practicum.shareit.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items/{itemId}/comment")
@Validated
public class CommentController {
    private final CommentClient commentClient;

    @PostMapping
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentCreateDto body) {
        return commentClient.addComment(userId, itemId, body);
    }
}
