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
    public UserDto findUsers(@PathVariable int userId) {
        return userService.findUser(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody User user, @PathVariable int userId) {
        return userService.updateUser(user, userId);
    }

    @PostMapping()
    public UserDto createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
    }

}
