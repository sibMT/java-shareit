package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    public ItemRequestDetailsDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestDto body) {
        return service.create(userId, body);
    }

    @GetMapping
    public List<ItemRequestDetailsDto> getOwn(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getOwn(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDetailsDto> getAllExceptOwn(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return service.getAllExceptOwn(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDetailsDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        return service.getById(userId, requestId);
    }

}
