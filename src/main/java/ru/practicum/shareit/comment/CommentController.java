package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateRequest;
import ru.practicum.shareit.comment.dto.CommentDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items/{itemId}/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @jakarta.validation.Valid @RequestBody CommentCreateRequest body) {
        return commentService.addComment(userId, itemId, body.getText());
    }

}
