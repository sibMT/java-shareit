package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("Owner не найден: id=" + ownerId));

        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NoSuchElementException("Request не найден: id=" + itemDto.getRequestId()));
        }

        Item toSave = itemMapper.toNewEntity(itemDto, owner, request);
        Item saved = itemRepository.save(toSave);
        return itemMapper.toItemDto(saved);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        Item existing = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item не найден: id=" + itemId));
        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Только owner может изменить item");
        }
        itemMapper.updateEntity(itemDto, existing);
        if (itemDto.getRequestId() != null) {
            ItemRequest req = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NoSuchElementException("Request не найден: id=" + itemDto.getRequestId()));
            existing.setRequest(req);
        }
        Item updated = itemRepository.save(existing);
        return itemMapper.toItemDto(updated);
    }

    @Override
    public ItemOwnerViewDto getItemById(Long requesterId, Long itemId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + requesterId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item не найден: id=" + itemId));

        boolean isOwner = item.getOwner().getId().equals(requesterId);
        LocalDateTime now = LocalDateTime.now();

        Booking last = null, next = null;
        if (isOwner) {
            last = bookingRepository
                    .findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(itemId, now, BookingStatus.APPROVED)
                    .orElse(null);
            next = bookingRepository
                    .findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(itemId, now, BookingStatus.APPROVED)
                    .orElse(null);
        }

        var comments = commentRepository.findAllByItem_IdOrderByCreatedDesc(itemId);
        return itemMapper.toOwnerViewDto(item, last, next, comments);
    }

    @Override
    public List<ItemDto> getItemsByOwner(Long ownerId) {
        return itemRepository.findItemByOwner_Id(ownerId).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null || text.isBlank()) return List.of();
        return itemRepository.searchItem(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }
}

