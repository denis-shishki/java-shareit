package ru.practicum.shareit.booking;

import lombok.Data;
import org.springframework.stereotype.Service;
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

@Service
@Data
public class BookingMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public Booking toBookingFromRequestsDto(BookingRequestDto bookingRequestDto, long userId) {
        User booker = new User();
        Item item = new Item();

        booker.setId(userId);
        item.setId(bookingRequestDto.getItemId());

        Booking booking = new Booking();
        booking.setId(0L); //Если не добавить эту строку, то при сохранении в бд у нас возвращается объект Booking с User booker у которого пустое поле name. Это очень странно. Нужно ли Во всех сущностях ставить примитивные типы полей вместо оберток?
        booking.setStartBooking(LocalDateTime.parse(bookingRequestDto.getStart(), formatter));
        booking.setEndBooking(LocalDateTime.parse(bookingRequestDto.getEnd(), formatter));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatusBooking(StatusBooking.WAITING);

        return booking;
    }

    public BookingResponseDto toResponseBookingDto(Booking booking) {
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

    public BookingForOwnerByItem toBookingForOwnerByItem(Booking booking) {
        return new BookingForOwnerByItem(booking.getId(),
                formatter.format(booking.getStartBooking()),
                formatter.format(booking.getEndBooking()),
                booking.getBooker().getId());
    }

}
