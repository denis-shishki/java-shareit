package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public abstract class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
