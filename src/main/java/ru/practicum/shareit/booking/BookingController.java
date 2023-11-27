package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestBody BookingRequestDto bookingDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateStatusBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable("bookingId") long bookingId,
                                                  @RequestParam Boolean approved) {
        return bookingService.updateStatusBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> findBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.findBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllBookingsByItemsOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                @RequestParam(value = "state", defaultValue = "ALL") String state,
                                                                @RequestParam(defaultValue = "0") Integer from,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        return bookingService.findAllBookingsByItemsOwner(userId, state, from, size);
    }

}
