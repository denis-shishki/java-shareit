package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long userId, long itemId);

    ItemWithBookingsDto findItem(long itemId, long userId);

    List<ItemWithBookingsDto> findAllItemForOwner(long userId);

    List<ItemDto> searchAvailableItem(String text);

    void checkItemByUser(long itemId, long userId);

    void checkExistItem(long itemId);

    ItemWithBookingsDto findItem(long itemId);

    CommentDto createComment(CommentDto commentDto, Long userId, Long itemId);
}
