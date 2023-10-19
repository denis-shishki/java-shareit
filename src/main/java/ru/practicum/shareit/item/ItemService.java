package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Item item, int userId);

    ItemDto updateItem(Item item, int userId, int itemId);

    ItemDto findItem(int itemId);

    List<ItemDto> findAllItemForOwner(int userId);

    List<ItemDto> searchAvailableItem(String text);
}
