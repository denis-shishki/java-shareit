package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void createRequest_whenUserExistAndResponseValid_thenReturnRequestDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Описание");
        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("Описание");

        when(itemRequestMapper.toItemRequest(requestDto, 0L)).thenReturn(itemRequest);
        when(requestRepository.save(itemRequest)).thenReturn(itemRequest);
        when(itemRequestMapper.toRequestDto(itemRequest)).thenReturn(requestDto);

        RequestDto dtoResponse = requestService.createRequest(0L, requestDto);

        assertEquals(requestDto, dtoResponse);
        verify(userService).checkExistUser(0L);
        verify(itemRequestMapper).toItemRequest(requestDto, 0L);
        verify(itemRequestMapper).toRequestDto(itemRequest);
    }

    @Test
    void createRequest_whenUserNonExisting_thenUserNotFoundExceptionThrow() {
        long userId = 0L;
        doThrow(NotFoundException.class).when(userService).checkExistUser(userId);

        assertThrows(NotFoundException.class, () -> requestService.createRequest(0L, new RequestDto()));

        verify(requestRepository, never()).save(new ItemRequest());
    }

    @Test
    void createRequest_whenUserNoValid_thenValidationException() {
        RequestDto requestDto = new RequestDto();
        long userId = 0L;

        assertThrows(ValidationException.class, () -> requestService.createRequest(userId, requestDto));

        verify(requestRepository, never()).save(Mockito.any(ItemRequest.class));
    }

    @Test
    void findAllRequestsForUser_whenUserNonExisting_thenUserNotFoundExceptionThrow() {
        long userId = 0L;
        doThrow(NotFoundException.class).when(userService).checkExistUser(userId);

        assertThrows(NotFoundException.class, () -> requestService.findAllRequestsForUser(0L));

        verify(requestRepository, never()).findAllByRequestor_IdIs(anyLong());
    }

    @Test
    void findAllRequestsForUser_whenUserExist_thenReturnItems() {
        long userId = 1L;
        List<ItemRequest> itemRequests = new ArrayList<>();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(userId);
        itemRequests.add(itemRequest);
        ItemRequestForResponseDto itemResponse = new ItemRequestForResponseDto();
        itemResponse.setId(userId);
        List<ItemForRequest> itemForRequests = new ArrayList<>();

        when(requestRepository.findAllByRequestor_IdIs(userId)).thenReturn(itemRequests);
        when(itemRequestMapper.toResponseDto(Mockito.any(ItemRequest.class))).thenReturn(itemResponse);
        when(itemService.findItemForRequest(userId)).thenReturn(itemForRequests);

        List<ItemRequestForResponseDto> requestForResponse = requestService.findAllRequestsForUser(userId);
        assertEquals(requestForResponse.get(0), itemResponse);

        verify(userService).checkExistUser(userId);
        verify(requestRepository).findAllByRequestor_IdIs(userId);
        verify(itemRequestMapper).toResponseDto(Mockito.any(ItemRequest.class));
    }

    @Test
    void findAllRequests_whenUserExist_returnRequests() {
        long userId = 1L;
        List<ItemRequest> itemRequests = new ArrayList<>();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(userId);
        itemRequests.add(itemRequest);
        Page<ItemRequest> itemRequestsPage = new PageImpl<>(itemRequests);
        ItemRequestForResponseDto itemResponse = new ItemRequestForResponseDto();
        itemResponse.setId(userId);
        List<ItemForRequest> itemForRequests = new ArrayList<>();

        when(requestRepository.findAllNonOwnerBySort(anyLong(), Mockito.any(Pageable.class))).thenReturn(itemRequestsPage);
        when(itemRequestMapper.toResponseDto(Mockito.any(ItemRequest.class))).thenReturn(itemResponse);
        when(itemService.findItemForRequest(userId)).thenReturn(itemForRequests);

        List<ItemRequestForResponseDto> requestForResponse = requestService.findAllRequests(userId, 0, 1);
        assertEquals(requestForResponse.get(0), itemResponse);

        verify(userService).checkExistUser(userId);
        verify(requestRepository).findAllNonOwnerBySort(anyLong(), Mockito.any(Pageable.class));
        verify(itemRequestMapper).toResponseDto(Mockito.any(ItemRequest.class));
    }

    @Test
    void findAllRequests_whenUserNonExist_henUserNotFoundExceptionThrow() {
        long userId = 0L;
        doThrow(NotFoundException.class).when(userService).checkExistUser(userId);

        assertThrows(NotFoundException.class, () -> requestService.findAllRequests(userId, 0, 1));

        verify(requestRepository, never()).findAllNonOwnerBySort(anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void findRequestById_whenUserAndRequestExist_returnItemRequestForResponse() {
        long requestId = 1L;
        long userId = 2L;
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        ItemRequestForResponseDto responseDto = new ItemRequestForResponseDto();
        responseDto.setId(requestId);
        ItemRequestForResponseDto itemResponse = new ItemRequestForResponseDto();
        itemResponse.setId(requestId);

        when(requestRepository.existsById(requestId)).thenReturn(true);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.toResponseDto(itemRequest)).thenReturn(itemResponse);

        ItemRequestForResponseDto result = requestService.findRequestById(requestId, userId);

        assertEquals(result, itemResponse);

        verify(requestRepository).findById(requestId);
        verify(userService).checkExistUser(userId);
    }
}