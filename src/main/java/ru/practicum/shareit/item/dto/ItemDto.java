package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Builder
@Data
public class ItemDto {
    private final int id;
    private final Integer ownerId;
    private final String name;
    private final String description;
    private final boolean available;
    private final ItemRequest itemRequest;
}
