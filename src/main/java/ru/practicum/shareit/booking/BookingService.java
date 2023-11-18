package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, long userId);

    BookingResponseDto updateStatusBooking(long userId, long bookingId, Boolean approved);

    void checkExistBooking(long bookingId);

    BookingResponseDto findBooking(long userId, long bookingId);

    List<BookingResponseDto> findBookingsByUser(long userId, String state);

    List<BookingResponseDto> findAllBookingsByItemsOwner(long userId, String state);

    void checkValidateBooking(BookingRequestDto bookingDto, long userId);
}
