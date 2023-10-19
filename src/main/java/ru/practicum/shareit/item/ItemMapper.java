package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public abstract class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .ownerId(item.getOwnerId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .itemRequest(item.getItemRequest())
                .build();
    }
}
