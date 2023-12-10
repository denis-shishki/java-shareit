package ru.practicum.shareit.request;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final ItemRequestMapper itemRequestMapper;
    private final RequestRepository requestRepository;
    private final ItemService itemService;

    @Transactional
    @Override
    public RequestDto createRequest(long userId, RequestDto requestDto) {
        userService.checkExistUser(userId);
        checkValidateResponse(requestDto);

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(requestDto, userId);

        return itemRequestMapper.toRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    @Transactional
    public List<ItemRequestForResponseDto> findAllRequestsForUser(long userId) {
        userService.checkExistUser(userId);

        List<ItemRequest> requests = requestRepository.findAllByRequestor_IdIs(userId);
        List<ItemRequestForResponseDto> requestForResponse = requests.stream()
                .map(itemRequestMapper::toResponseDto)
                .collect(Collectors.toList());
        addItemResponse(requestForResponse);

        return requestForResponse;
    }

    @Transactional
    @Override
    public List<ItemRequestForResponseDto> findAllRequests(long userId, Integer from, Integer size) {
        Pageable pageable = Paginator.getPageable(from, size, "created");
        userService.checkExistUser(userId);

        Page<ItemRequest> itemRequests = requestRepository.findAllNonOwnerBySort(userId, pageable);
        List<ItemRequestForResponseDto> requestsDto = itemRequests.stream()
                .map(itemRequestMapper::toResponseDto)
                .collect(Collectors.toList());
        addItemResponse(requestsDto);

        return requestsDto;
    }

    @Transactional
    @Override
    public ItemRequestForResponseDto findRequestById(long requestId, long userId) {
        userService.checkExistUser(userId);
        checkExistRequest(requestId);

        ItemRequest itemRequest = requestRepository.findById(requestId).orElseThrow();
        ItemRequestForResponseDto responseDto = itemRequestMapper.toResponseDto(itemRequest);
        addItemResponse(responseDto);

        return responseDto;
    }

    public void checkExistRequest(long id) {
        if (!requestRepository.existsById(id)) throw new NotFoundException("Запроса с таким id не существует");
    }

    private void addItemResponse(List<ItemRequestForResponseDto> responsesDto) {
        responsesDto.forEach(itemRequestDto -> {
            long requestId = itemRequestDto.getId();
            itemRequestDto.setItems(itemService.findItemForRequest(requestId));
        });
    }

    private void addItemResponse(ItemRequestForResponseDto responseDto) {
        long requestId = responseDto.getId();
        responseDto.setItems(itemService.findItemForRequest(requestId));
    }

    protected void checkValidateResponse(RequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            throw new ValidationException("Запрос не может быть пустым");
        }
    }
}
