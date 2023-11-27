package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(long userId, RequestDto requestDto);

    List<ItemRequestForResponseDto> findAllRequestsForUser(long userId);

    List<ItemRequestForResponseDto> findAllRequests(long userId, Integer from, Integer size);

    ItemRequestForResponseDto findRequestById(long requestId, long userId);

}
