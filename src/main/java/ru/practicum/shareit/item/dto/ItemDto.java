package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;


@Data
@AllArgsConstructor
public class ItemDto {
    protected final Long id;
    protected Long ownerId;
    protected final String name;
    protected final String description;
    protected final Boolean available;
    protected final ItemRequest itemRequest;
    protected List<CommentDto> comments;
}
