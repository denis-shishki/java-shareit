package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.StatusBooking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private final long itemId = 1L;
    private final String startDataTime = "2200-10-12T22:22:22";
    private final String endDataTime = "2200-10-12T23:22:22";
    private final LocalDateTime startLocalDateTime = LocalDateTime.of(2200, 10, 12, 22, 22, 22);
    private final LocalDateTime endLocalDataTime = LocalDateTime.of(2200, 10, 12, 23, 22, 22);


    @Test
    void createBooking_whenBookingValid_thenReturnBooking() {
        long itemId = 1L;
        long userId = 2L;
        long ownerId = 3L;
        User booker = new User(userId, "name", "email@");
        BookingRequestDto requestDto = new BookingRequestDto(startDataTime, endDataTime, itemId);
        ItemWithBookingsDto itemDto = new ItemWithBookingsDto();
        itemDto.setOwnerId(ownerId);
        Item item = new Item();
        Booking booking = new Booking(4L, startLocalDateTime, endLocalDataTime, booker, item, StatusBooking.APPROVED);

        when(itemService.findItem(itemId)).thenReturn(itemDto);
        when(itemService.existsItemByIdAndAvailableIsTrue(itemId)).thenReturn(true);
        when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);

        BookingResponseDto response = bookingService.createBooking(requestDto, userId);
        assertEquals(startDataTime, response.getStart());
        assertEquals(endDataTime, response.getEnd());
    }

    @Test
    void createBooking_whenUserIsOwner_thenReturnNotFoundException() {
        long itemId = 1L;
        long userId = 2L;
        BookingRequestDto requestDto = new BookingRequestDto(startDataTime, endDataTime, itemId);
        ItemWithBookingsDto itemDto = new ItemWithBookingsDto();
        itemDto.setOwnerId(userId);

        when(itemService.findItem(itemId)).thenReturn(itemDto);

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(requestDto, userId));
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenReturnValidationException() {
        long itemId = 1L;
        long userId = 2L;
        long ownerId = 3L;
        BookingRequestDto requestDto = new BookingRequestDto(startDataTime, endDataTime, itemId);
        ItemWithBookingsDto itemDto = new ItemWithBookingsDto();
        itemDto.setOwnerId(ownerId);

        when(itemService.findItem(itemId)).thenReturn(itemDto);
        when(itemService.existsItemByIdAndAvailableIsTrue(itemId)).thenReturn(false);

        assertThrows(ValidationException.class, () -> bookingService.createBooking(requestDto, userId));
    }

    @Test
    void updateStatusBooking_whenRequestApprovedAndBookingNotApproved_thenReturnBooking() {
        long userId = 1L;
        long bookingId = 2L;
        boolean isApproved = true;
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking booking = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.WAITING);
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingResponseDto response = bookingService.updateStatusBooking(userId, bookingId, isApproved);
        assertEquals(StatusBooking.APPROVED, response.getStatus());
    }

    @Test
    void updateStatusBooking_whenRequestRejectedAndBookingNotRejected_thenReturnBooking() {
        long userId = 1L;
        long bookingId = 2L;
        boolean isApproved = false;
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking booking = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.WAITING);
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingResponseDto response = bookingService.updateStatusBooking(userId, bookingId, isApproved);
        assertEquals(StatusBooking.REJECTED, response.getStatus());
    }

    @Test
    void updateStatusBooking_whenRequestApprovedAndBookingApproved_thenReturnValidationException() {
        long userId = 1L;
        long bookingId = 2L;
        boolean isApproved = true;
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking booking = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.APPROVED);
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateStatusBooking(userId, bookingId, isApproved));
    }

    @Test
    void updateStatusBooking_whenRequestRejectedAndBookingRejected_thenReturnValidationException() {
        long userId = 1L;
        long bookingId = 2L;
        boolean isApproved = false;
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking booking = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.REJECTED);
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.updateStatusBooking(userId, bookingId, isApproved));
    }

    @Test
    void updateStatusBooking_whenUserNotOwner_thenReturnNotFoundException() {
        long userId = 1L;
        long bookingId = 2L;
        long otherId = 3L;
        Boolean isApproved = true;
        User user = new User(otherId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking booking = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.WAITING);
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.updateStatusBooking(userId, bookingId, isApproved));
    }

    @Test
    void findBooking_whenBookingNotExist_thenReturnNotFoundException() {
        long userId = 1L;
        long bookingId = 2L;

        when(bookingRepository.existsById(bookingId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.findBooking(userId, bookingId));
    }

    @Test
    void findBooking_whenUser_thenReturnBooking() {
        long userId = 1L;
        long bookingId = 2L;
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking booking = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.existsById(bookingId)).thenReturn(true);

        BookingResponseDto response = bookingService.findBooking(userId, bookingId);
        assertEquals(booking.getId(), response.getId());
    }

    @Test
    void findBooking_whenUserNotOwnerItem_thenNotFoundException() {
        long userId = 1L;
        long otherId = 3L;
        long bookingId = 2L;
        User user = new User(otherId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking booking = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.existsById(bookingId)).thenReturn(true);

        assertThrows(NotFoundException.class, () -> bookingService.findBooking(userId, bookingId));
    }

    @Test
    void findBookingsByUser_whenStateAll_thenReturnBookings() {
        long userId = 1L;
        long itemId = 2L;
        long bookingId = 2L;
        int from = 0;
        int size = 10;
        String state = "ALL";
        Pageable pageable = Paginator.getPageable(from, size, "endBooking");
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking bookingFirst = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.WAITING);
        Booking bookingSecond = new Booking(bookingId, startLocalDateTime.plusHours(2), endLocalDataTime.plusHours(2), user, item, StatusBooking.WAITING);
        List<Booking> bookings = List.of(bookingFirst, bookingSecond);

        when(bookingRepository.findAllByBookerIdIs(userId, pageable)).thenReturn(bookings);

        List<BookingResponseDto> response = bookingService.findBookingsByUser(userId, state, from, size);
        assertEquals(bookings.size(), response.size());
    }

    @Test
    void findBookingsByUser_whenStateCURRENT_thenReturnBookings() {
        long userId = 1L;
        long itemId = 2L;
        long bookingId = 2L;
        int from = 0;
        int size = 10;
        String state = "CURRENT";
        LocalDateTime start = LocalDateTime.of(2000, 10, 12, 22, 22, 22);
        Pageable pageable = Paginator.getPageable(from, size, "endBooking");
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking bookingFirst = new Booking(bookingId, start, endLocalDataTime, user, item, StatusBooking.WAITING);
        Booking bookingSecond = new Booking(bookingId, startLocalDateTime.plusHours(2), endLocalDataTime.plusHours(2), user, item, StatusBooking.WAITING);
        List<Booking> bookings = List.of(bookingFirst, bookingSecond);

        when(bookingRepository.findAllByBookerIdIs(userId, pageable)).thenReturn(bookings);

        List<BookingResponseDto> response = bookingService.findBookingsByUser(userId, state, from, size);
        assertEquals(1, response.size());
    }

    @Test
    void findBookingsByUser_whenStatePAST_thenReturnBookings() {
        long userId = 1L;
        long itemId = 2L;
        long bookingId = 2L;
        int from = 0;
        int size = 10;
        String state = "PAST";
        LocalDateTime start = LocalDateTime.of(2000, 10, 12, 22, 22, 22);
        LocalDateTime end = LocalDateTime.of(2002, 10, 12, 22, 22, 22);
        Pageable pageable = Paginator.getPageable(from, size, "endBooking");
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking bookingFirst = new Booking(bookingId, start, end, user, item, StatusBooking.WAITING);
        Booking bookingSecond = new Booking(bookingId, startLocalDateTime.plusHours(2), endLocalDataTime.plusHours(2), user, item, StatusBooking.WAITING);
        List<Booking> bookings = List.of(bookingFirst, bookingSecond);

        when(bookingRepository.findAllByBookerIdIs(userId, pageable)).thenReturn(bookings);

        List<BookingResponseDto> response = bookingService.findBookingsByUser(userId, state, from, size);
        assertEquals(1, response.size());
    }

    @Test
    void findBookingsByUser_whenStateFUTURE_thenReturnBookings() {
        long userId = 1L;
        long itemId = 2L;
        long bookingId = 2L;
        int from = 0;
        int size = 10;
        String state = "FUTURE";
        LocalDateTime start = LocalDateTime.of(2000, 10, 12, 22, 22, 22);
        LocalDateTime end = LocalDateTime.of(2002, 10, 12, 22, 22, 22);
        Pageable pageable = Paginator.getPageable(from, size, "endBooking");
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking bookingFirst = new Booking(bookingId, start, end, user, item, StatusBooking.WAITING);
        Booking bookingSecond = new Booking(bookingId, startLocalDateTime.plusHours(2), endLocalDataTime.plusHours(2), user, item, StatusBooking.WAITING);
        List<Booking> bookings = List.of(bookingFirst, bookingSecond);

        when(bookingRepository.findAllByBookerIdIs(userId, pageable)).thenReturn(bookings);

        List<BookingResponseDto> response = bookingService.findBookingsByUser(userId, state, from, size);
        assertEquals(1, response.size());
    }

    @Test
    void findBookingsByUser_whenStateWAITING_thenReturnBookings() {
        long userId = 1L;
        long itemId = 2L;
        long bookingId = 2L;
        int from = 0;
        int size = 10;
        String state = "WAITING";
        Pageable pageable = Paginator.getPageable(from, size, "endBooking");
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking bookingFirst = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.WAITING);
        Booking bookingSecond = new Booking(bookingId, startLocalDateTime.plusHours(2), endLocalDataTime.plusHours(2), user, item, StatusBooking.WAITING);
        List<Booking> bookings = List.of(bookingFirst, bookingSecond);

        when(bookingRepository.findAllByBookerIdIs(userId, pageable)).thenReturn(bookings);

        List<BookingResponseDto> response = bookingService.findBookingsByUser(userId, state, from, size);
        assertEquals(2, response.size());
    }

    @Test
    void findBookingsByUser_whenStateREJECTED_thenReturnBookings() {
        long userId = 1L;
        long itemId = 2L;
        long bookingId = 2L;
        int from = 0;
        int size = 10;
        String state = "REJECTED";
        Pageable pageable = Paginator.getPageable(from, size, "endBooking");
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking bookingFirst = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.REJECTED);
        Booking bookingSecond = new Booking(bookingId, startLocalDateTime.plusHours(2), endLocalDataTime.plusHours(2), user, item, StatusBooking.REJECTED);
        List<Booking> bookings = List.of(bookingFirst, bookingSecond);

        when(bookingRepository.findAllByBookerIdIs(userId, pageable)).thenReturn(bookings);

        List<BookingResponseDto> response = bookingService.findBookingsByUser(userId, state, from, size);
        assertEquals(2, response.size());
    }

    @Test
    void findBookingsByUser_whenStateUnknown_thenReturnValidationException() {
        long userId = 1L;
        long itemId = 2L;
        long bookingId = 2L;
        int from = 0;
        int size = 10;
        String state = "----";
        Pageable pageable = Paginator.getPageable(from, size, "endBooking");
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking bookingFirst = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.REJECTED);
        Booking bookingSecond = new Booking(bookingId, startLocalDateTime.plusHours(2), endLocalDataTime.plusHours(2), user, item, StatusBooking.REJECTED);
        List<Booking> bookings = List.of(bookingFirst, bookingSecond);

        when(bookingRepository.findAllByBookerIdIs(userId, pageable)).thenReturn(bookings);

        assertThrows(ValidationException.class, () -> bookingService.findBookingsByUser(userId, state, from, size));
    }

    @Test
    void findAllBookingsByItemsOwner() {
        long userId = 1L;
        long itemId = 2L;
        long bookingId = 2L;
        int from = 0;
        int size = 10;
        String state = "ALL";
        Pageable pageable = Paginator.getPageable(from, size, "endBooking");
        User user = new User(userId, "name", "description");
        Item item = new Item(itemId, user, "name", "description", true);
        Booking bookingFirst = new Booking(bookingId, startLocalDateTime, endLocalDataTime, user, item, StatusBooking.WAITING);
        Booking bookingSecond = new Booking(bookingId, startLocalDateTime.plusHours(2), endLocalDataTime.plusHours(2), user, item, StatusBooking.WAITING);
        List<Booking> bookings = List.of(bookingFirst, bookingSecond);

        when(bookingRepository.findAllBookingsByItemsOwner(userId, pageable)).thenReturn(bookings);

        List<BookingResponseDto> response = bookingService.findAllBookingsByItemsOwner(userId, state, from, size);
        assertEquals(bookings.size(), response.size());
    }
}