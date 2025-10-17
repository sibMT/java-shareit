package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @RequestBody ItemDto dto) {
        return itemService.createItem(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto dto) {
        return itemService.updateItem(ownerId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemOwnerViewDto getById(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                    @PathVariable Long itemId) {
        return itemService.getItemById(requesterId, itemId);
    }

    @GetMapping
    public List<ItemDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.searchItem(text);
    }
}
