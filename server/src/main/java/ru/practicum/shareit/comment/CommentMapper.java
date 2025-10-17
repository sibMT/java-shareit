package ru.practicum.shareit.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    @Mapping(target = "authorName", source = "author.name")
    CommentDto toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "item", source = "item")
    Comment toEntity(CommentCreateDto dto, User author, Item item);

}
