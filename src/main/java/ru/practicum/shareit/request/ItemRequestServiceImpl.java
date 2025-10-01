package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto create(Long requesterId, Create dto) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + requesterId));
        ItemRequest entity = itemRequestMapper.toEntity(dto, requester);
        ItemRequest saved = requestRepository.save(entity);
        return itemRequestMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getOwn(Long requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + requesterId));
        return requestRepository.findByRequester_IdOrderByCreatedDesc(requesterId)
                .stream().map(itemRequestMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllExceptOwn(Long requesterId, int from, int size) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + requesterId));
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        return requestRepository.findByRequester_IdNotOrderByCreatedDesc(requesterId, page)
                .map(itemRequestMapper::toDto).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDetailsDto getById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + userId));

        ItemRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request не найден: id=" + requestId));

        List<ItemDto> items = itemRepository.findByRequest_Id(requestId)
                .stream().map(itemMapper::toItemDto).toList();
        return itemRequestMapper.toDetails(req, items);
    }
}
