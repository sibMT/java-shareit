package ru.practicum.shareit.item;

import org.mapstruct.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "requestId", source = "request.id")
    ItemDto toItemDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "owner", ignore = true),
            @Mapping(target = "request", ignore = true)
    })
    void updateEntity(ItemDto dto, @MappingTarget Item target);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "name", source = "dto.name"),
            @Mapping(target = "description", source = "dto.description"),
            @Mapping(target = "available", source = "dto.available"),
            @Mapping(target = "owner", source = "owner"),
            @Mapping(target = "request", source = "request")
    })
    Item toNewEntity(ItemDto dto, User owner, ItemRequest request);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingShortDto toBookingShortDto(Booking booking);

    @Mapping(target = "authorName", source = "author.name")
    CommentDto toCommentDto(Comment comment);

    default ItemDetailsDto toItemDetailsDto(Item item, List<CommentDto> comments) {
        if (item == null) return null;
        return ItemDetailsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .comments(comments != null ? comments : List.of())
                .build();
    }

    default ItemOwnerViewDto toOwnerViewDto(Item item, Booking last, Booking next, List<Comment> comments) {
        if (item == null) return null;
        return ItemOwnerViewDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(last != null ? toBookingShortDto(last) : null)
                .nextBooking(next != null ? toBookingShortDto(next) : null)
                .comments(comments == null ? List.of() : comments.stream().map(this::toCommentDto).toList())
                .build();
    }
}


