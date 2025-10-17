package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public ItemRequestDetailsDto create(Long requesterId, ItemRequestDto dto) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + requesterId));
        ItemRequest entity = itemRequestMapper.toEntity(dto, requester);
        ItemRequest saved = requestRepository.save(entity);
        return itemRequestMapper.toDetails(saved, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDetailsDto> getOwn(Long requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + requesterId));
        List<ItemRequest> requests = requestRepository.findByRequester_IdOrderByCreatedDesc(requesterId);
        return collectAnswers(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDetailsDto> getAllExceptOwn(Long requesterId, int from, int size) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + requesterId));
        Pageable page = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requests = requestRepository.findByRequester_IdNotOrderByCreatedDesc(requesterId, page)
                .getContent();
        return collectAnswers(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDetailsDto getById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + userId));

        ItemRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Request не найден: id=" + requestId));

        List<ItemAnswerDto> items = itemRepository.findByRequest_Id(requestId).stream()
                .map(itemRequestMapper::toAnswer)
                .toList();
        return itemRequestMapper.toDetails(req, items);
    }

    private List<ItemRequestDetailsDto> collectAnswers(List<ItemRequest> requests) {
        if (requests.isEmpty()) return List.of();

        List<Long> ids = requests.stream().map(ItemRequest::getId).toList();
        List<Item> items = itemRepository.findByRequest_IdIn(ids);

        Map<Long, List<ItemAnswerDto>> byRequestId = items.stream()
                .collect(Collectors.groupingBy(
                        it -> it.getRequest().getId(),
                        Collectors.mapping(itemRequestMapper::toAnswer, Collectors.toList())
                ));
        return requests.stream()
                .map(r -> itemRequestMapper.toDetails(r, byRequestId.getOrDefault(r.getId(), List.of())))
                .toList();
    }
}
