package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    @Test
    void toUserDto() {
        long userId = 1L;
        String name = "name";
        String email = "email@";
        User user = new User(userId, name, email);

        UserDto userReturn = UserMapper.toUserDto(user);

        assertEquals(userReturn.getId(), user.getId());
        assertEquals(userReturn.getName(), user.getName());
        assertEquals(userReturn.getEmail(), user.getEmail());
    }

    @Test
    void toUser() {
        long userId = 1L;
        String name = "name";
        String email = "email@";
        UserDto userDto = new UserDto(userId, name, email);

        User userReturn = UserMapper.toUser(userDto);

        assertEquals(userReturn.getId(), userDto.getId());
        assertEquals(userReturn.getName(), userDto.getName());
        assertEquals(userReturn.getEmail(), userDto.getEmail());
    }
}