package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class ItemRequestDto {
    private int id;
    private final int userId;
    private final String name;
    private final String description;
}
