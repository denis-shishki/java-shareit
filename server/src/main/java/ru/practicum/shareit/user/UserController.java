package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public Collection<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto findUser(@PathVariable long userId) {
        return userService.findUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable int userId) {
        return userService.updateUser(userDto, userId);
    }

    @PostMapping()
    public UserDto createUser(@RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
    }

}
