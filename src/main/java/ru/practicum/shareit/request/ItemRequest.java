package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private int id;
    private final int requestor;
    private final String name;
    private final String description;
    private final LocalDateTime created;
}
