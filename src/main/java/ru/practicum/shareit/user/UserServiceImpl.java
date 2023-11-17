package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validateUser(user);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto updateUser(UserDto userDto, long userId) {
        checkExistUser(userId);
        userDto.setId(userId);
        User userUpdate = UserMapper.toUser(userDto);
        User user = Optional.of(userRepository.findById(userId)).get().orElseThrow();

        if (userUpdate.getName() != null) {
            user.setName(userUpdate.getName());
        }
        if (userUpdate.getEmail() != null) {
            if (!userUpdate.getEmail().contains("@"))
                throw new ValidationException("Электронная почта указана некорректно");
            user.setEmail(userUpdate.getEmail());
        }

        return UserMapper.toUserDto(userRepository.save(user));

    }

    public void deleteUser(long id) {
        checkExistUser(id);
        userRepository.deleteById(id);
    }

    @Override
    public UserDto findUser(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) throw new NotFoundException("Пользователь с заданным id не найден");
        return UserMapper.toUserDto(userOptional.get());
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void checkExistUser(long id) {
        if (!userRepository.existsById(id)) throw new NotFoundException("Пользователя с таким id не существует");
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
