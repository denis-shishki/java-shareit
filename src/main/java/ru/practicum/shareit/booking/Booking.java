package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.constants.StatusBooking;

import java.time.LocalDateTime;

@Data
public class Booking {
    public int id;
    public final LocalDateTime start;
    public final LocalDateTime end;
    public final int booker;
    public final int item;
    public final boolean available;
    public final StatusBooking statusBooking;
    public final String review; // отзыв о бронировании
}