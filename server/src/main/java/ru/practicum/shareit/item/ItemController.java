package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId) {
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto findItem(@PathVariable long itemId,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findItem(itemId, userId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> findAllItemForOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "0") Integer from,
                                                         @RequestParam(defaultValue = "10") Integer size) {
        return itemService.findAllItemForOwner(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItem(@RequestParam String text,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return itemService.searchAvailableItem(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        return itemService.createComment(commentDto, userId, itemId);
    }
}
