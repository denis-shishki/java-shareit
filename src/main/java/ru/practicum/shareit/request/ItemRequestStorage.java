package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ItemRequestStorage {
    private final Map<Integer, ItemRequest> itemRequests = new HashMap<>();
    private int id = 1;

    public ItemRequest createItemRequest(ItemRequest itemRequest) {
        itemRequest.setId(id++);
        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequest;
    }

    public ItemRequest findItemRequest(int itemRequestId) {
        return itemRequests.get(itemRequestId);
    }

    public ItemRequest updateItemRequest(ItemRequest itemRequest) {
        itemRequests.put(itemRequest.getId(), itemRequest);
        return itemRequests.get(itemRequest.getId());
    }

    public void deleteItemRequest(int itemRequestId) { // нужно ли удалять все связанные записи или это сделаем в бд
        itemRequests.remove(itemRequestId);
    }
}
