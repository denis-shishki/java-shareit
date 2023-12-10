package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.group.BasicInfo;
import ru.practicum.shareit.group.InfoForUpdate;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> postUser(@RequestBody @Validated(BasicInfo.class) UserRequestDto requestDto) {
        log.info("Creating user {}, userId={}", requestDto);
        return userClient.postUser(requestDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive long userId) {
        return userClient.deleteUser(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody @Validated(InfoForUpdate.class) UserRequestDto requestDto,
                                             @PathVariable long userId) {
        return userClient.patchUser(userId, requestDto);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUser(@PathVariable long userId) {
        return userClient.getUser(userId);
    }
}
