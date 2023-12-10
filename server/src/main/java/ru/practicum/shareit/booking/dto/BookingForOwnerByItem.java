package ru.practicum.shareit.booking.dto;

import lombok.Data;

@Data
public class BookingForOwnerByItem {
    private long id;

    private String start;

    private String end;

    private long bookerId;

    public BookingForOwnerByItem(long id, String start, String end, long bookerId) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.bookerId = bookerId;
    }
}
