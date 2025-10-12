package ru.practicum.shareit.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.Create;
import ru.practicum.shareit.comment.Update;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    @NotBlank(message = "Текст комментария обязателен", groups = Create.class)
    @Size(max = 1000, message = "Максимум 1000 символов", groups = {Create.class, Update.class})
    private String text;
    private String authorName;
    private LocalDateTime created;
}
