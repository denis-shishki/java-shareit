package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, int userId);

    void deleteUser(int id);

    UserDto findUser(int id);

    List<UserDto> findAllUsers();

    void checkUser(int id);
}
