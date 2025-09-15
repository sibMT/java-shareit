package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long requesterId, ItemRequestCreateDto dto);

    List<ItemRequestDto> getOwn(Long requesterId);

    List<ItemRequestDto> getAllExceptOwn(Long requesterId, int from, int size);

    ItemRequestDetailsDto getById(Long userId, Long requestId);
}
