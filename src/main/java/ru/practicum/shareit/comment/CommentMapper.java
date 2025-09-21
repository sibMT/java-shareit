package ru.practicum.shareit.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentDto;

@UtilityClass
public class CommentMapper {

    public CommentDto toDto(Comment c) {
        if (c == null) return null;
        return new CommentDto(
                c.getId(),
                c.getText(),
                c.getAuthor().getName(),
                c.getCreated()
        );
    }
}
