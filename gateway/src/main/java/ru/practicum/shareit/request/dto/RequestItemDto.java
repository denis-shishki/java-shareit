package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
@AllArgsConstructor
public class RequestItemDto {
    private Long id;
    @NotEmpty
    private String description;

    private String created;
}
