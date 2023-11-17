package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.StatusBooking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    private final UserService userService;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        checkValidateItem(itemDto);
        userService.checkExistUser(userId);
        itemDto.setOwnerId(userId);
        Item item = itemMapper.toItem(itemDto);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long userId, long itemId) {
        userService.checkExistUser(userId);
        checkItemByUser(itemId, userId);
        itemDto.setOwnerId(userId);
        Item itemUpdate = itemMapper.toItem(itemDto);
        Item item = Optional.of(itemRepository.findById(itemId)).get().orElseThrow();

        if (itemUpdate.getName() != null) {
            item.setName(itemUpdate.getName());
        }
        if (itemUpdate.getDescription() != null) {
            item.setDescription(itemUpdate.getDescription());
        }
        if (itemUpdate.getAvailable() != null) {
            item.setAvailable(itemUpdate.getAvailable());
        }

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemWithBookingsDto findItem(long itemId, long userId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        boolean isOwner = false;
        if (itemOptional.isEmpty()) throw new NotFoundException("Вещи с указанным Id не найдено");

        Item item = itemOptional.get();

        if (item.getOwner().getId() == userId) {
            isOwner = true;
        }


        return itemMapper.toItemForOwnerByItemDto(itemOptional.get(), isOwner);

    }

    @Override
    public List<ItemWithBookingsDto> findAllItemForOwner(long userId) {
        userService.checkExistUser(userId);
        List<Item> items = itemRepository.findItemByOwnerId(userId);
        return items.stream()
                .map(item -> itemMapper.toItemForOwnerByItemDto(item, true))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItem(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();

        return itemRepository.searchByNameAndDescriptionAndAvailable(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void checkItemByUser(long itemId, long userId) {
        if (!itemRepository.existsItemByIdAndOwnerId(itemId, userId)) {
            throw new NotFoundException("Запрашиваемая вещь отсутствует у данного пользователя");
        }
    }

    @Override
    public void checkExistItem(long itemId) {
        if (!itemRepository.existsById(itemId)) throw new NotFoundException("Данный пользователь не найден");
    }

    @Override
    public ItemWithBookingsDto findItem(long itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) throw new NotFoundException("Вещи с указанным Id не найдено");
        return itemMapper.toItemForOwnerByItemDto(itemOptional.get(), false);
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Long userId, Long itemId) {
        checkValidateComment(commentDto);
        checkExistItem(itemId);
        userService.checkExistUser(userId);

        List<Booking> bookings = bookingRepository.findBookingByItemIdAndBookerId(itemId, userId);

        bookings.stream()
                .filter(b -> b.getStatusBooking() == StatusBooking.APPROVED &&
                        b.getEndBooking().isBefore(LocalDateTime.now()))
                .findFirst().orElseThrow(() -> new ValidationException("Чтобы оставить комментарий необходимо наличие завершенного бронирования"));


        Comment comment = CommentMapper.toComment(commentDto, itemId, userId);
        comment = commentRepository.save(comment);
        User owner = userRepository.findById(userId).orElseThrow();
        commentDto = CommentMapper.toCommentDto(comment);
        commentDto.setAuthorName(owner.getName());

        return commentDto;
    }

    private void checkValidateComment(CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            log.error("Ошибка добавления комментария");
            throw new ValidationException("Комментарий не может быть пустым.");
        }
        commentDto.setCreated(LocalDateTime.now());
    }

    private void checkValidateItem(ItemDto item) {
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
