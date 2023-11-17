package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingForOwnerByItem;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Getter
@Setter
public class ItemWithBookingsDto extends ItemDto {

    private BookingForOwnerByItem lastBooking;

    private BookingForOwnerByItem nextBooking;

    public ItemWithBookingsDto(Long id,
                               Long ownerId,
                               String name,
                               String description,
                               Boolean available,
                               ItemRequest itemRequest,
                               List<CommentDto> commentsDto) {
        super(id, ownerId, name, description, available, itemRequest, commentsDto);
    }
}