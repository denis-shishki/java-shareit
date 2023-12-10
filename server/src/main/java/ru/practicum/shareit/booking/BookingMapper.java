package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingForOwnerByItem;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.StatusBooking;
import ru.practicum.shareit.item.dto.ItemForBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserShort;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Data
@AllArgsConstructor
public class BookingMapper {

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static Booking toBookingFromRequestsDto(BookingRequestDto bookingRequestDto, long userId) {
        User booker = new User();
        Item item = new Item();

        booker.setId(userId);
        item.setId(bookingRequestDto.getItemId());

        Booking booking = new Booking();
        booking.setId(0L); //Если не добавить эту строку, то при сохранении в бд у нас возвращается объект Booking с User booker у которого пустое поле name
        booking.setStartBooking(LocalDateTime.parse(bookingRequestDto.getStart(), formatter));
        booking.setEndBooking(LocalDateTime.parse(bookingRequestDto.getEnd(), formatter));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatusBooking(StatusBooking.WAITING);

        return booking;
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
