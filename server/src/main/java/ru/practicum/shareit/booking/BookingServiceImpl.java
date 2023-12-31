package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.StatusBooking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserService userService;

    private final ItemService itemService;


    @Transactional
    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, long userId) {
        checkValidateBooking(bookingRequestDto, userId);
        Booking booking = BookingMapper.toBookingFromRequestsDto(bookingRequestDto, userId);
        return BookingMapper.toResponseBookingDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto updateStatusBooking(long userId,
                                                  long bookingId,
                                                  Boolean approved) {
        userService.checkExistUser(userId);
        checkExistBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow();

        if (booking.getItem().getOwner().getId() != userId) {
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

    @Transactional
    @Override
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

    @Transactional
    @Override
    public List<BookingResponseDto> findBookingsByUser(long userId, String state, Integer from, Integer size) {
        Pageable pageable = Paginator.getPageable(from, size, "endBooking");
        userService.checkExistUser(userId);
        List<Booking> booking = bookingRepository.findAllByBookerIdIs(userId, pageable);

        return sortedBookings(booking, state.toUpperCase()).stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingResponseDto> findAllBookingsByItemsOwner(long userId, String state, Integer from, Integer size) {
        Pageable pageable = Paginator.getPageable(from, size, "endBooking");
        userService.checkExistUser(userId);
        List<Booking> booking = bookingRepository.findAllBookingsByItemsOwner(userId, pageable);

        return sortedBookings(booking, state.toUpperCase()).stream()
                .map(BookingMapper::toResponseBookingDto)
                .collect(Collectors.toList());
    }

    private List<Booking> sortedBookings(List<Booking> bookings, String state) {
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case "ALL":
                return bookings;
            case "CURRENT":
                return bookings.stream()
                        .filter(b -> now.isAfter(b.getStartBooking()) && now.isBefore(b.getEndBooking()))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(b -> now.isAfter(b.getEndBooking()))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(b -> now.isBefore(b.getStartBooking()))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(b -> b.getStatusBooking() == StatusBooking.WAITING)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(b -> b.getStatusBooking() == StatusBooking.REJECTED)
                        .collect(Collectors.toList());
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    private void checkValidateBooking(BookingRequestDto bookingDto, long userId) {
        userService.checkExistUser(userId);
        itemService.checkExistItem(bookingDto.getItemId());
        long ownerId = itemService.findItem(bookingDto.getItemId()).getOwnerId();

        if (ownerId == userId) {
            throw new NotFoundException("Владелец не может забронировать собственную вещь");
        }

        if (!itemService.existsItemByIdAndAvailableIsTrue(bookingDto.getItemId())) {
            throw new ValidationException("Запрашиваемая вещь уже занята");
        }
    }

    private void checkExistBooking(long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Бронирования с таким id не существует");
        }
    }
}
