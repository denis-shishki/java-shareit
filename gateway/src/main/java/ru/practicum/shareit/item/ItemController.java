package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> postItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody @Valid ItemRequestDto requestDto) {
        return itemClient.postItem(requestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemRequestDto requestDto,
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable @Positive long itemId) {
        return itemClient.patchItem(requestDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable @Positive long itemId,
                                          @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "10") @Positive int size) {
        return itemClient.getAllItemForOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItem(@RequestHeader("X-Sharer-User-Id") long userId, //раньше здесь не было юзерИд
                                                      @RequestParam(defaultValue = "") String text,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "10") @Positive int size) {
        return itemClient.searchAvailableItem(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestBody @Valid CommentRequestDto requestDto,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable @Positive long itemId) {
        return itemClient.postComment(requestDto, userId, itemId);
    }
}
