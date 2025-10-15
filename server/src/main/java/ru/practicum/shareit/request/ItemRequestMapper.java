package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemRequestMapper {

    ItemRequestDto toDto(ItemRequest itemRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    ItemRequest toEntity(ItemRequestDto dto, User requester);

    @Mapping(target = "items", source = "items")
    ItemRequestDetailsDto toDetails(ItemRequest itemRequest, List<ItemAnswerDto> items);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemAnswerDto toAnswer(Item item);

}
