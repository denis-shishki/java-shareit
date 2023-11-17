package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.StatusBooking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ItemMapper {
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemDto toItemDto(Item item) {
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        List<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        return new ItemDto(item.getId(),
                item.getOwner().getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest(),
                commentsDto);
    }

    public Item toItem(ItemDto itemDto) {
        User user = new User();
        user.setId(itemDto.getOwnerId());

        return new Item(itemDto.getId(),
                user,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getItemRequest());
    }

    public ItemWithBookingsDto toItemForOwnerByItemDto(Item item, boolean isOwner) {
        LocalDateTime now = LocalDateTime.now();

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        List<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        ItemWithBookingsDto itemForOwner = new ItemWithBookingsDto(item.getId(),
                item.getOwner().getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest(),
                commentsDto);

        if (isOwner) {

            List<Booking> bookings = bookingRepository.findBookingsByItem(item);
            if (!bookings.isEmpty()) {

                Booking lastBooking = bookings.stream()
                        .filter(b -> b.getStartBooking().isBefore(now))
                        .max(Comparator.comparing(Booking::getStartBooking))
                        .orElse(null);

                Booking nextBooking = bookings.stream()
                        .filter(b -> b.getStartBooking().isAfter(now))
                        .filter(b -> b.getStatusBooking() == StatusBooking.APPROVED)
                        .min(Comparator.comparing(Booking::getStartBooking))
                        .orElse(null);

                if (lastBooking != null) {
                    itemForOwner.setLastBooking(BookingMapper.toBookingForOwnerByItem(lastBooking));
                }
                if (nextBooking != null) {
                    itemForOwner.setNextBooking(BookingMapper.toBookingForOwnerByItem(nextBooking));
                }
            }
        }

        return itemForOwner;

    }
}
