package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;

@RestController
@RequestMapping("/items/{itemId}/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable("itemId") Long itemId,
                                 @RequestBody CommentCreateDto body) {

        return commentService.addComment(userId, itemId, body);
    }
}
