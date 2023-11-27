package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.StatusBooking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
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
    private final BookingMapper bookingMapper;

    public ItemDto toItemDto(Item item) {
        Long requestId = null;
        if (item.getItemRequest() != null) {
            requestId = item.getItemRequest().getId();
        }

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        List<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());


        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setOwnerId(item.getOwner().getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(requestId);
        itemDto.setComments(commentsDto);

        return itemDto;
    }

    public Item toItem(ItemDto itemDto) {
        User user = new User();
        user.setId(itemDto.getOwnerId());

        Item item = new Item(itemDto.getId(),
                user,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable());

        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemDto.getRequestId());
            item.setItemRequest(itemRequest);
        }

        return item;
    }

    public ItemWithBookingsDto toItemForOwnerByItemDto(Item item, boolean isOwner) {
        LocalDateTime now = LocalDateTime.now();

        Long requestId = null;
        if (item.getItemRequest() != null) {
            requestId = item.getItemRequest().getId();
        }

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        List<CommentDto> commentsDto = comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        ItemWithBookingsDto itemForOwner = new ItemWithBookingsDto(item.getId(),
                item.getOwner().getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId,
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
                    itemForOwner.setLastBooking(bookingMapper.toBookingForOwnerByItem(lastBooking));
                }
                if (nextBooking != null) {
                    itemForOwner.setNextBooking(bookingMapper.toBookingForOwnerByItem(nextBooking));
                }
            }
        }
        return itemForOwner;
    }

    public ItemForRequest toItemForRequest(Item item) {
        ItemForRequest itemForRequest = new ItemForRequest();

        Long requestId = null;
        if (item.getItemRequest() != null) {
            requestId = item.getItemRequest().getId();
        }

        itemForRequest.setId(item.getId());
        itemForRequest.setName(item.getName());
        itemForRequest.setDescription(item.getDescription());
        itemForRequest.setRequestId(requestId);
        itemForRequest.setAvailable(item.getAvailable());

        return itemForRequest;
    }
}
