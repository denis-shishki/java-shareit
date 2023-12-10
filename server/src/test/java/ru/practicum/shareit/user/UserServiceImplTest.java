package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void updateUser_whenUserExistAndValid_thenReturnUserDto() {
        long userId = 1L;
        UserDto userRequest = new UserDto(userId, "name", "email@");
        User oldUser = new User(userId, "old name", "old email@");
        User saveUser = new User(userId, "name", "email@");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(saveUser)).thenReturn(saveUser);
        when(userRepository.existsById(userId)).thenReturn(true);

        UserDto responseUser = userService.updateUser(userRequest, userId);
        assertEquals(userRequest, responseUser);
    }

    @Test
    public void updateUser_whenEmailNonValid_thenValidationException() {
        long userId = 1L;
        UserDto userRequest = new UserDto(userId, "name", "email");
        User oldUser = new User(userId, "old name", "old email@");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.existsById(userId)).thenReturn(true);

        assertThrows(ValidationException.class, () -> userService.updateUser(userRequest, userId));
        verify(userRepository, never()).save(Mockito.any(User.class));
    }

    @Test
    public void updateUser_whenEmailNonExist_thenReturnUserDto() {
        long userId = 1L;
        UserDto userRequest = new UserDto(userId, "name", null);
        User oldUser = new User(userId, "old name", "old email@");
        User saveUser = new User(userId, "name", "old email@");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(saveUser)).thenReturn(saveUser);
        when(userRepository.existsById(userId)).thenReturn(true);

        UserDto responseUser = userService.updateUser(userRequest, userId);
        assertEquals(userRequest.getName(), responseUser.getName());
        assertEquals(responseUser.getEmail(), saveUser.getEmail());
    }

    @Test
    public void updateUser_whenNameNonExist_thenReturnUserDto() {
        long userId = 1L;
        UserDto userRequest = new UserDto(userId, null, "email@");
        User oldUser = new User(userId, "old name", "old email@");
        User saveUser = new User(userId, "old name", "email@");

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(saveUser)).thenReturn(saveUser);
        when(userRepository.existsById(userId)).thenReturn(true);

        UserDto responseUser = userService.updateUser(userRequest, userId);
        assertEquals(userRequest.getEmail(), responseUser.getEmail());
        assertEquals(responseUser.getName(), saveUser.getName());
    }

    @Test
    public void createUser_whenUserValid_thenReturnUser() {
        UserDto userRequest = new UserDto(null, "name", "email@");
        User userWithoutId = new User(null, "name", "email@");
        User userWithId = new User(1L, "name", "email@");
        UserDto userResponse = new UserDto(1L, "name", "email@");

        when(userRepository.save(userWithoutId)).thenReturn(userWithId);

        UserDto userReturn = userService.createUser(userRequest);
        assertEquals(userReturn, userResponse);
    }

    @Test
    public void deleteUser_whenUserIdExist() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);
        assertTrue(true);
    }

    @Test
    public void deleteUser_whenUserIdNoExist() {
        long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    public void findUser_whenUserExist_thenReturnUser() {
        long userId = 1L;
        User user = new User(userId, "name", "email@");
        Optional<User> userOptional = Optional.of(user);
        UserDto userDto = new UserDto(userId, "name", "email@");

        when(userRepository.findById(userId)).thenReturn(userOptional);

        UserDto userReturn = userService.findUser(userId);
        assertEquals(userReturn, userDto);
    }

    @Test
    public void findUser_whenUserNotExist_thenNotFoundException() {
        long userId = 1L;

        Optional<User> userOptional = Optional.empty();

        when(userRepository.findById(userId)).thenReturn(userOptional);
        assertThrows(NotFoundException.class, () -> userService.findUser(userId));
    }

    @Test
    public void findAllUsers_whenExistTwoUser_thenReturnUsers() {
        List<User> users = List.of(new User(), new User());

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> returnUsers = userService.findAllUsers();
        assertEquals(returnUsers.size(), users.size());
    }


}