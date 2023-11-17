package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.constants.StatusBooking;

@Data
public class BookingRequestDto {
    private long id;

    private String start;

    private String end;

    private long bookerId;

    private long itemId;

    private StatusBooking status;

    public BookingRequestDto(long id, String start, String end, long bookerId, long itemId, StatusBooking status) {
        if (status == null) {
            status = StatusBooking.WAITING;
        }

        this.id = id;
        this.start = start;
        this.end = end;
        this.bookerId = bookerId;
        this.itemId = itemId;
        this.status = status;
    }
}
