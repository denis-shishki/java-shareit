package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @Test
    @SneakyThrows
    void createItem() {
        long itemId = 1L;
        long userId = 2L;
        ItemDto itemRequestDto = new ItemDto(null, "name", "description", true);
        ItemDto itemResponseDto = new ItemDto(itemId, "name", "description", true);

        Mockito.when(itemService.createItem(itemRequestDto, userId)).thenReturn(itemResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemResponseDto)));

        Mockito.verify(itemService, Mockito.times(1)).createItem(itemRequestDto, userId);
    }

    @Test
    @SneakyThrows
    void updateItem() {
        long itemId = 1L;
        long userId = 2L;
        ItemDto itemRequestDto = new ItemDto(itemId, "name", "description", true);
        ItemDto itemResponseDto = new ItemDto(itemId, "name", "description", true);

        Mockito.when(itemService.updateItem(itemRequestDto, userId, itemId)).thenReturn(itemResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemResponseDto)));

        Mockito.verify(itemService, Mockito.times(1)).updateItem(itemRequestDto, userId, itemId);
    }

    @Test
    @SneakyThrows
    void findItem() {
        long itemId = 1L;
        long userId = 2L;
        ItemWithBookingsDto itemResponseDto = new ItemWithBookingsDto(itemId, 1L, "name", "description", true, 1L, new ArrayList<>());

        Mockito.when(itemService.findItem(itemId, userId)).thenReturn(itemResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemResponseDto)));

        Mockito.verify(itemService, Mockito.times(1)).findItem(itemId, userId);
    }

    @Test
    @SneakyThrows
    void findAllItemForOwner() {
        long userId = 2L;
        int from = 1;
        int size = 10;
        List<ItemWithBookingsDto> itemsWithBookingsResponseDto = new ArrayList<ItemWithBookingsDto>();

        Mockito.when(itemService.findAllItemForOwner(userId, from, size)).thenReturn(itemsWithBookingsResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemsWithBookingsResponseDto)));

        Mockito.verify(itemService, Mockito.times(1)).findAllItemForOwner(userId, from, size);
    }

    @Test
    @SneakyThrows
    void searchAvailableItem() {
        String text = "search text";
        int from = 1;
        int size = 10;
        List<ItemDto> itemsResponseDto = new ArrayList<ItemDto>();

        Mockito.when(itemService.searchAvailableItem(text, from, size)).thenReturn(itemsResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                        .param("text", "search text")
                        .param("from", "1")
                        .param("size", "10"))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(itemsResponseDto)));

        Mockito.verify(itemService, Mockito.times(1)).searchAvailableItem(text, from, size);
    }

    @Test
    @SneakyThrows
    void createComment() {
        long userId = 1L;
        long itemId = 2L;
        CommentDto commentRequest = new CommentDto(null, "text", "name", null);
        CommentDto commentResponse = new CommentDto(5L, "text", "name", null);

        Mockito.when(itemService.createComment(commentRequest, userId, itemId)).thenReturn(commentResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/" + itemId + "/comment")
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(commentResponse)));

        Mockito.verify(itemService, Mockito.times(1)).createComment(commentRequest, userId, itemId);
    }
}