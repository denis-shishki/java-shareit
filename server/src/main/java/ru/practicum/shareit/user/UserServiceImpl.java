package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotUniqueEmailException;
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

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);

        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (Throwable throwable) {
            throw new NotUniqueEmailException("Такая почта уже зарегистрирована");
        }
    }

    @Transactional
    @Override
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

        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (Throwable throwable) {
            throw new NotUniqueEmailException("Такая почта уже зарегистрирована");
        }
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        checkExistUser(id);
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public UserDto findUser(long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) throw new NotFoundException("Пользователь с заданным id не найден");
        return UserMapper.toUserDto(userOptional.get());
    }

    @Transactional
    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void checkExistUser(long id) {
        if (!userRepository.existsById(id)) throw new NotFoundException("Пользователя с таким id не существует");
    }

}
