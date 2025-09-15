package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;


public interface ItemService {
    ItemDto createItem(Long ownerId, ItemCreateDto itemDto);

    ItemDto updateItem(Long ownerId, Long itemId, ItemUpdateDto itemDto);

    ItemOwnerViewDto getItemById(Long requesterId, Long itemId);

    List<ItemDto> getItemsByOwner(Long ownerId);

    List<ItemDto> searchItem(String text);
}
