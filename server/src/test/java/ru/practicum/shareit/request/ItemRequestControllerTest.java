package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.request.dto.ItemRequestForResponseDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private RequestService requestService;

    @Test
    @SneakyThrows
    void createRequest() {
        long itemResponseId = 1L;
        long userId = 2L;
        RequestDto itemRequestDto = new RequestDto(null, "name", "description");
        RequestDto itemRequestResponseDto = new RequestDto(itemResponseId, "name", "description");

        Mockito.when(requestService.createRequest(userId, itemRequestDto)).thenReturn(itemRequestResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestResponseDto)));

        Mockito.verify(requestService, Mockito.times(1)).createRequest(userId, itemRequestDto);
    }

    @Test
    @SneakyThrows
    void findAllRequestForUser() {
        long userId = 2L;
        List<ItemRequestForResponseDto> responseItems = new ArrayList<>();

        Mockito.when(requestService.findAllRequestsForUser(userId)).thenReturn(responseItems);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests").header("X-Sharer-User-Id", userId))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseItems)));

        Mockito.verify(requestService, Mockito.times(1)).findAllRequestsForUser(userId);
    }

    @Test
    @SneakyThrows
    void findAllRequest() {
        long userId = 2L;
        int from = 1;
        int size = 10;
        List<ItemRequestForResponseDto> responseItems = new ArrayList<>();

        Mockito.when(requestService.findAllRequests(userId, from, size)).thenReturn(responseItems);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(responseItems)));

        Mockito.verify(requestService, Mockito.times(1)).findAllRequests(userId, from, size);
    }

    @Test
    @SneakyThrows
    void findRequestById() {
        long itemRequestId = 1L;
        long userId = 2L;
        ItemRequestForResponseDto itemRequestResponseDto = new ItemRequestForResponseDto();

        Mockito.when(requestService.findRequestById(itemRequestId, userId)).thenReturn(itemRequestResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/" + itemRequestId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemRequestResponseDto)));

        Mockito.verify(requestService, Mockito.times(1)).findRequestById(itemRequestId, userId);
    }
}