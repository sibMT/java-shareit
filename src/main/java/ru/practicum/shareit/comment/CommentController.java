package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
            @Validated(Create.class) @RequestBody CommentDto dto) {
        return commentService.addComment(userId, itemId, dto);
    }

}
