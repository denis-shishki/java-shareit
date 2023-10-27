package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int itemId) {
        return itemService.updateItem(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItem(@PathVariable int itemId) {
        return itemService.findItem(itemId);
    }

    @GetMapping
    public List<ItemDto> findAllItemForOwner(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.findAllItemForOwner(userId); // может здесь отдавать полноценный Item? Владельцу же отправляем
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItem(@RequestParam String text) {
        return itemService.searchAvailableItem(text);
    }
}
