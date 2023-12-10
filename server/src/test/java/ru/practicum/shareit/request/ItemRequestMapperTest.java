package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class ItemRequestMapperTest {

    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

    @Test
    void toItemRequest() {
        long userId = 2L;
        String description = "description";
        RequestDto requestDto = new RequestDto(null, description, null);

        ItemRequest returnRequest = itemRequestMapper.toItemRequest(requestDto, userId);
        assertNotNull(returnRequest.getId());
        assertEquals(returnRequest.getDescription(), description);
        assertNotNull(returnRequest.getCreated());
    }

    @Test
    void toResponseDto() {
        long itemId = 1L;
        String description = "description";
        ItemRequest itemRequest = new ItemRequest(itemId, new User(), description, LocalDateTime.now());

        ItemRequestForResponseDto itemRequestForResponseDto = itemRequestMapper.toResponseDto(itemRequest);
        assertEquals(itemRequestForResponseDto.getId(), itemId);
        assertEquals(itemRequestForResponseDto.getDescription(), description);
        assertNotNull(itemRequestForResponseDto.getCreated());
    }

    @Test
    void toRequestDto() {
        long itemId = 1L;
        String description = "description";
        LocalDateTime localDateTime = LocalDateTime.of(1999, 10, 12, 22, 22, 22);
        String dateTime = "1999-10-12T22:22:22";

        ItemRequest itemRequest = new ItemRequest(itemId, new User(), description, localDateTime);

        RequestDto requestDto = itemRequestMapper.toRequestDto(itemRequest);
        assertEquals(requestDto.getCreated(), dateTime);
        assertEquals(requestDto.getDescription(), description);
        assertEquals(requestDto.getId(), itemId);
    }
}