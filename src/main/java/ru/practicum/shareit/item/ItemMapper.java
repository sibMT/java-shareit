package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class ItemMapper {


    public ItemDto toItemDto(Item item) {
        if (item == null) {
            return null;
        }
        Long ownerId = item.getOwner() != null ? item.getOwner().getId() : null;
        Long requestId = item.getRequest() != null ? item.getRequest().getId() : null;

        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                ownerId,
                requestId);
    }

    public Item toItem(ItemCreateDto dto, User owner, ItemRequest request) {
        if (dto == null) return null;

        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);
        item.setRequest(request);
        return item;
    }

    public Item toItem(ItemUpdateDto dto, User owner, ItemRequest request) {
        if (dto == null) return null;

        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);
        item.setRequest(request);
        return item;
    }

    public ItemOwnerViewDto toOwnerViewDto(Item item,
                                           Booking last,
                                           Booking next,
                                           List<Comment> comments) {
        return ItemOwnerViewDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(last == null ? null : new BookingShortDto(
                        last.getId(), last.getStart(), last.getEnd(), last.getBooker().getId()))
                .nextBooking(next == null ? null : new BookingShortDto(
                        next.getId(), next.getStart(), next.getEnd(), next.getBooker().getId()))
                .comments(comments.stream()
                        .map(CommentMapper::toDto)
                        .toList())
                .build();
    }
}
