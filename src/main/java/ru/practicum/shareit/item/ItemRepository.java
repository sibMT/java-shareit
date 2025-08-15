package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<Item> findItemById(Long id);

    List<Item> findItemByOwnerId(Long ownerId);

    List<Item> searchItem(String text);
}
