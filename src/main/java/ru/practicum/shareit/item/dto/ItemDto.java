package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Builder
@Data
public class ItemDto {
    private final int id;
    private Integer ownerId;
    private final String name;
    private final String description;
    private final Boolean available;
    private final ItemRequest itemRequest;
}
