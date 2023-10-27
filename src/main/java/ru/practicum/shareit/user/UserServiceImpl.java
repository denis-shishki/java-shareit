package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validateUser(user);
        checkUniquenessEmail(user);
        return UserMapper.toUserDto(userStorage.createUser(user));
    }

    public UserDto updateUser(UserDto userDto, int userId) {
        checkUser(userId);
        userDto.setId(userId);
        User user = UserMapper.toUser(userDto);
        if (user.getName() != null && user.getEmail() != null) {
            return UserMapper.toUserDto(userStorage.updateUser(user));
        }
        if (user.getName() != null) {
            userStorage.updateUserName(userId, user.getName());
        } else if (user.getEmail() != null) {
            checkUniquenessEmail(user);
            userStorage.updateUserEmail(userId, user.getEmail());
        }

        return UserMapper.toUserDto(userStorage.findUser(userId));
    }

    public void deleteUser(int id) {
        checkUser(id);
        userStorage.deleteUser(id);
    }

    @Override
    public UserDto findUser(int id) {
        return UserMapper.toUserDto(userStorage.findUser(id));
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userStorage.findAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void checkUser(int id) {
        User user = userStorage.findUser(id);
        if (user == null) throw new NotFoundException("Данный пользователь не найден");
    }

    private void checkUniquenessEmail(User user) {
        List<User> users = userStorage.findAllUsers();
        if (users == null) return;

        if (user.getId() != null) {
            users = users.stream()
                    .filter(user1 -> !Objects.equals(user1.getId(), user.getId()))
                    .collect(Collectors.toList());
        }

        List<String> emails = users.stream()
                .map(User::getEmail)
                .collect(Collectors.toList());

        if (emails.contains(user.getEmail()))
            throw new NotUniqueEmailException("Пользователь с таким Email уже существует");
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("Ошибка добавления пользователя.");
            throw new ValidationException("Имя пользователя не может быть пустым");
        } else if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Ошибка добавления пользователя.");
            throw new ValidationException("Электронная почта указана некорректно");
        }
    }
}
