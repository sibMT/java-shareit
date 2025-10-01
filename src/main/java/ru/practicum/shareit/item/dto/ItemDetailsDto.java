package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDetailsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private List<CommentDto> comments;
}
