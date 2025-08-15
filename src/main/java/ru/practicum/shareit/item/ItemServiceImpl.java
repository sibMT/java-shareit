package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long ownerId, ItemCreateDto itemDto) {
        User owner = userRepository.findUserById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("Owner не найден: id=" + ownerId));

        Item item = ItemMapper.toItem(itemDto, owner, null);
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long ownerId, Long itemId, ItemUpdateDto itemDto) {
        Item existing = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item не найден: id=" + itemId));

        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Только owner может изменить item");
        }

        if (itemDto.getName() != null) existing.setName(itemDto.getName());
        if (itemDto.getDescription() != null) existing.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) existing.setAvailable(itemDto.getAvailable());

        itemRepository.save(existing);
        return ItemMapper.toItemDto(existing);
    }

    @Override
    public ItemDto getItemById(Long requesterId, Long itemId) {
        Item item = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item not found: id=" + itemId));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemRepository.findItemByOwnerId(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) return List.of();
        return itemRepository.searchItem(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }
}
