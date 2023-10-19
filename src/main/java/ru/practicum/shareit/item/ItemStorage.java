package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private int id = 1;

    public Item createItem(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    public List<Item> findAllItemForOwner(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId() == userId)
                .collect(Collectors.toList());
    }

    public List<Item> searchAvailableItem(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text)))
                .collect(Collectors.toList());
    }

    public void updateNameItem(int itemId, String name) {
        Item item = items.get(itemId);
        item.setName(name);
        items.put(itemId, item);
    }

    public void updateDescriptionItem(int itemId, String description) {
        Item item = items.get(itemId);
        item.setDescription(description);
        items.put(itemId, item);
    }

    public void updateAvailableItem(int itemId, boolean isAvailable) {
        Item item = items.get(itemId);
        item.setAvailable(isAvailable);
        items.put(itemId, item);
    }

    public Item findItem(int itemId) {
        return items.get(itemId);
    }
}
