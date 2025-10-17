package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDetailsDto create(Long requesterId, ItemRequestDto dto);

    List<ItemRequestDetailsDto> getOwn(Long requesterId);

    List<ItemRequestDetailsDto> getAllExceptOwn(Long requesterId, int from, int size);

    ItemRequestDetailsDto getById(Long userId, Long requestId);
}
