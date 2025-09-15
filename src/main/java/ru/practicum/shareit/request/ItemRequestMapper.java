package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public final class ItemRequestMapper {
    private ItemRequestMapper() {
    }

    public static ItemRequest toEntity(ItemRequestCreateDto dto, User requester) {
        return ItemRequest.builder()
                .description(dto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest r) {
        return ItemRequestDto.builder()
                .id(r.getId())
                .description(r.getDescription())
                .created(r.getCreated())
                .build();
    }

    public static ItemRequestDetailsDto toDetails(ItemRequest r, List<ItemDto> items) {
        return ItemRequestDetailsDto.builder()
                .id(r.getId())
                .description(r.getDescription())
                .created(r.getCreated())
                .items(items)
                .build();
    }
}
