package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class RequestDto {
    private int id;
    private final User requestor;
    private final String name;
    private final String description;
}
