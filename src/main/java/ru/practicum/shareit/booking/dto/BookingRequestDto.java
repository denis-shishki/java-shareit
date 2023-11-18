package ru.practicum.shareit.booking.dto;

import lombok.Data;

@Data
public class BookingRequestDto {

    private String start;

    private String end;

    private long itemId;

}
