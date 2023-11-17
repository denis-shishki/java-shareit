package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, long userId);

    void deleteUser(long id);

    UserDto findUser(long id);

    List<UserDto> findAllUsers();

    void checkExistUser(long id);
}
