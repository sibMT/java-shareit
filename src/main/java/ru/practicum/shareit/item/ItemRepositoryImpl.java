package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> storage = new HashMap<>();
    private final Map<Long, List<Item>> itemsByOwner = new HashMap<>();
    private long idCounter = 0;

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(++idCounter);
        }
        storage.put(item.getId(), item);
        Long ownerId = item.getOwner().getId();

        if (!itemsByOwner.containsKey(ownerId)) {
            itemsByOwner.put(ownerId, new ArrayList<>());
        }
        itemsByOwner.get(ownerId).add(item);

        return item;
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Item> findItemByOwnerId(Long ownerId) {
        return itemsByOwner.getOrDefault(ownerId, new ArrayList<>());
    }

    @Override
    public List<Item> searchItem(String text) {
        String lower = text.toLowerCase();
        List<Item> result = new ArrayList<>();
        for (Item item : storage.values()) {
            if (item.isAvailable() && item.getName().toLowerCase().contains(lower) ||
                    item.getDescription().toLowerCase().contains(lower)) {
                result.add(item);
            }
        }
        return result;
    }
}
