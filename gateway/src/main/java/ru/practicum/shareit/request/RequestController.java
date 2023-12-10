package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestBody @Valid RequestItemDto requestDto) {
        return requestClient.postRequest(requestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsForUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestClient.getAllRequestsForUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable long requestId) {
        return requestClient.findRequestById(userId, requestId);
    }
}
