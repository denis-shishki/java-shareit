package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingForOwnerByItem;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShort;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BookingMapper {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static Booking toBookingFromRequestsDto(BookingRequestDto bookingRequestDto) {
        User booker = new User();
        Item item = new Item();

        booker.setId(bookingRequestDto.getBookerId());
        item.setId(bookingRequestDto.getItemId());

        return new Booking(bookingRequestDto.getId(),
                LocalDateTime.parse(bookingRequestDto.getStart(), formatter),
                LocalDateTime.parse(bookingRequestDto.getEnd(), formatter),
                booker,
                item,
                bookingRequestDto.getStatus());
    }

    public static BookingResponseDto toResponseBookingDto(Booking booking) {
        UserShort owner = new UserShort(booking.getBooker().getId(),
                booking.getBooker().getName());
        ItemForBooking item = new ItemForBooking(booking.getItem().getId(),
                booking.getItem().getName());

        return new BookingResponseDto(booking.getId(),
                formatter.format(booking.getStartBooking()),
                formatter.format(booking.getEndBooking()),
                owner,
                item,
                booking.getStatusBooking());
    }

    public static BookingForOwnerByItem toBookingForOwnerByItem(Booking booking) {
        return new BookingForOwnerByItem(booking.getId(),
                formatter.format(booking.getStartBooking()),
                formatter.format(booking.getEndBooking()),
                booking.getBooker().getId());
    }

}
