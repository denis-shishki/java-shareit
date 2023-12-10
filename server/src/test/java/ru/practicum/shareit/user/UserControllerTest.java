package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Test
    @SneakyThrows
    void findAllUsers() {
        long userId = 1L;
        UserDto userRequestDto = new UserDto(userId, "new name", "new email@");
        List<UserDto> users = List.of(userRequestDto);


        Mockito.when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(users)));

        Mockito.verify(userService, Mockito.times(1)).findAllUsers();

    }

    @Test
    @SneakyThrows
    void findUser() {
        long userId = 1L;
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).findUser(userId);
    }

    @Test
    @SneakyThrows
    void updateUser() {
        long userId = 1L;
        UserDto userRequestDto = new UserDto(1L, "new name", "new email@");
        UserDto userResponseDto = new UserDto(1L, "new name", "new email@");

        Mockito.when(userService.updateUser(userRequestDto, userId)).thenReturn(userResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userResponseDto)));

        Mockito.verify(userService, Mockito.times(1)).updateUser(userRequestDto, userId);

    }

    @Test
    @SneakyThrows
    void createUser() {
        UserDto userRequestDto = new UserDto(null, "name", "email@");
        UserDto userResponseDto = new UserDto(1L, "name", "email@");

        Mockito.when(userService.createUser(userRequestDto)).thenReturn(userResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .content(objectMapper.writeValueAsString(userRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userResponseDto)));

        Mockito.verify(userService, Mockito.times(1)).createUser(userRequestDto);
    }

    @Test
    @SneakyThrows
    void deleteUser() {
        long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1")
                        .param("userId", "1"))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1)).deleteUser(userId);
    }
}