package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.StatusBooking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final UserService userService;

    private final ItemService itemService;

    private final ItemRepository itemRepository;

    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Long userId) {
        checkValidateBooking(bookingRequestDto, userId);
        Booking booking = BookingMapper.toBookingFromRequestsDto(bookingRequestDto);
        return BookingMapper.toResponseBookingDto(bookingRepository.save(booking));
    }

    public BookingResponseDto updateStatusBooking(long userId,
                                                  long bookingId,
                                                  Boolean approved) {
        userService.checkExistUser(userId);
        checkExistBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        if (approved == null) {
            throw new ValidationException("Нужно указать необходимый статус для бронирования");
        } else if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Статус бронирования может менять только владелец вещи");
        }

        if (approved) {
            if (booking.getStatusBooking().equals(StatusBooking.APPROVED)) {
                throw new ValidationException("Бронирование уже подтверждено");
            }
            booking.setStatusBooking(StatusBooking.APPROVED);
        } else {
            if (booking.getStatusBooking().equals(StatusBooking.REJECTED)) {
                throw new ValidationException("Бронирование уже отклонено");
            }
            booking.setStatusBooking(StatusBooking.REJECTED);
        }

        return BookingMapper.toResponseBookingDto(bookingRepository.save(booking));
    }

    public void checkExistBooking(long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Бронирования с таким id не существует");
        }
    }

    public BookingResponseDto findBooking(long userId, long bookingId) {
        checkExistBooking(bookingId);
        userService.checkExistUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.toResponseBookingDto(booking);
        } else {
            throw new NotFoundException("У вас нет бронирований с запрашиваемым предметом");
        }
    }

    public List<BookingResponseDto> findBookingsByUser(long userId, String state) {
        userService.checkExistUser(userId);
        List<Booking> booking = bookingRepository.findAllByBookerIdIs(userId);

        return sortedBookings(booking, state).stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    public List<BookingResponseDto> findAllBookingsByItemsOwner(long userId, String state) {
        userService.checkExistUser(userId);
        List<Booking> booking = bookingRepository.findAllBookingsByItemsOwner(userId);

        return sortedBookings(booking, state).stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> sortedBookings(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case "ALL":
                return bookings.stream()
                        .sorted((b1, b2) -> b2.getStartBooking().compareTo(b1.getStartBooking()))
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookings.stream()
                        .filter(b -> now.isAfter(b.getStartBooking()) && now.isBefore(b.getEndBooking()))
                        .sorted((b1, b2) -> b2.getStartBooking().compareTo(b1.getStartBooking()))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(b -> now.isAfter(b.getEndBooking()))
                        .sorted((b1, b2) -> b2.getStartBooking().compareTo(b1.getStartBooking()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(b -> now.isBefore(b.getStartBooking()))
                        .sorted((b1, b2) -> b2.getStartBooking().compareTo(b1.getStartBooking()))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(b -> b.getStatusBooking() == StatusBooking.WAITING)
                        .sorted((b1, b2) -> b2.getStartBooking().compareTo(b1.getStartBooking()))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(b -> b.getStatusBooking() == StatusBooking.REJECTED)
                        .sorted((b1, b2) -> b2.getStartBooking().compareTo(b1.getStartBooking()))
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    public void checkValidateBooking(BookingRequestDto bookingDto, Long userId) {
        userService.checkExistUser(userId);
        bookingDto.setBookerId(userId);
        itemService.checkExistItem(bookingDto.getItemId());
        long ownerId = itemService.findItem(bookingDto.getItemId()).getOwnerId();

        if (ownerId == userId) {
            throw new NotFoundException("Владелец не может забронировать собственную вещь");
        }

        if (!itemRepository.existsItemByIdAndAvailableIsTrue(bookingDto.getItemId())) {
            throw new ValidationException("Запрашиваемая вещь уже занята");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Не указан период аренды");
        }

        LocalDateTime start = LocalDateTime.parse(bookingDto.getStart(), BookingMapper.formatter);
        LocalDateTime end = LocalDateTime.parse(bookingDto.getEnd(), BookingMapper.formatter);

        if (start.isAfter(end) || start.equals(end)) {
            throw new ValidationException("Время начала использования не может быть позже или равен времени окончания");
        } else if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало использования не может быть в прошедшем времени");
        }
    }
}
