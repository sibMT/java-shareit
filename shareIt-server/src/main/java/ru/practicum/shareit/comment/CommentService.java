package ru.practicum.shareit.comment;

import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;

public interface CommentService {
    CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto);
}
