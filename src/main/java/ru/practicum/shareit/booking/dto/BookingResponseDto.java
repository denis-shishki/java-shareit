package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.constants.StatusBooking;
import ru.practicum.shareit.item.dto.ItemForBooking;
import ru.practicum.shareit.user.dto.UserShort;

@Data
@AllArgsConstructor
public class BookingResponseDto {
    private long id;

    private String start;

    private String end;

    private UserShort booker;

    private ItemForBooking item;

    private StatusBooking status;
}
