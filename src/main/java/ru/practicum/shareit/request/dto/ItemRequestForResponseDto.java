package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemForRequest;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemRequestForResponseDto {
    private Long id;
    private String description;
    private String created;
    private List<ItemForRequest> items;
}
