package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto createItem(ItemDto itemDto, int userId) {
        userService.checkUser(userId);
        Item item = ItemMapper.toItem(itemDto);

        checkValidateItem(item);
        item.setOwnerId(userId);
        return ItemMapper.toItemDto(itemStorage.createItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int userId, int itemId) {
        userService.checkUser(userId);
        checkItemByUser(userId, itemId);
        itemDto.setOwnerId(userId);
        Item item = ItemMapper.toItem(itemDto);

        if (item.getName() != null) itemStorage.updateNameItem(itemId, item.getName());
        if (item.getDescription() != null) itemStorage.updateDescriptionItem(itemId, item.getDescription());
        if (item.getAvailable() != null) itemStorage.updateAvailableItem(itemId, item.getAvailable());
        return ItemMapper.toItemDto(itemStorage.findItem(itemId));
    }

    @Override
    public ItemDto findItem(int itemId) {
        return ItemMapper.toItemDto(itemStorage.findItem(itemId));
    }

    @Override
    public List<ItemDto> findAllItemForOwner(int userId) { //если вернется пустая коллекция? стрим не сломается?
        userService.checkUser(userId);

        return itemStorage.findAllItemForOwner(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItem(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();

        return itemStorage.searchAvailableItem(text.toLowerCase()).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkItemByUser(int userId, int itemId) {
        List<Item> itemsByUser = itemStorage.findAllItemForOwner(userId);

        Item findItem = itemsByUser.stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElse(null);
        if (findItem == null) throw new NotFoundException("Запрашиваемая вещь отсутствует у пользователя");
    }

    private void checkValidateItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Предмета вещи не может быть пустым");
        } else if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Описание предмета не может быть пустым");
        } else if (item.getAvailable() == null) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Необходимо выбрать статус для предмета");
        }
    }
}
