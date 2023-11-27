package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Service
public class ItemRequestMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public ItemRequest toItemRequest(RequestDto requestDto, long userId) {
        ItemRequest itemRequest = new ItemRequest();
        User user = new User();
        user.setId(userId);

        itemRequest.setId(0L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription(requestDto.getDescription());
        itemRequest.setRequestor(user);

        return itemRequest;
    }

    public ItemRequestForResponseDto toResponseDto(ItemRequest itemRequest) {
        ItemRequestForResponseDto responseDto = new ItemRequestForResponseDto();

        responseDto.setId(itemRequest.getId());
        responseDto.setDescription(itemRequest.getDescription());
        responseDto.setCreated(formatter.format(itemRequest.getCreated()));

        return responseDto;
    }

    public RequestDto toRequestDto(ItemRequest itemRequest) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(itemRequest.getId());
        requestDto.setCreated(formatter.format(itemRequest.getCreated()));
        requestDto.setDescription(itemRequest.getDescription());

        return requestDto;
    }
}
