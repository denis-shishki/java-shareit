package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int userId, int itemId);

    ItemDto findItem(int itemId);

    List<ItemDto> findAllItemForOwner(int userId);

    List<ItemDto> searchAvailableItem(String text);
}
