package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.constants.StatusBooking;
import ru.practicum.shareit.item.dto.ItemForBooking;
import ru.practicum.shareit.user.dto.UserShort;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    private final String startDataTime = "1999-10-12'T'22:22:22";
    private final String endDataTime = "1999-10-12'T'23:22:22";

    @Test
    @SneakyThrows
    void createBooking() {
        long userId = 1L;
        long itemId = 2L;
        UserShort user = new UserShort(userId, "name");
        ItemForBooking item = new ItemForBooking(itemId, "name");
        BookingRequestDto bookingRequestDto = new BookingRequestDto(startDataTime, endDataTime, itemId);
        BookingResponseDto bookingResponseDto = new BookingResponseDto(5L, startDataTime, endDataTime, user, item, StatusBooking.APPROVED);

        when(bookingService.createBooking(bookingRequestDto, userId)).thenReturn(bookingResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingResponseDto)));
    }

    @SneakyThrows
    @Test
    void updateStatusBooking() {
        long userId = 1L;
        long itemId = 2L;
        long bookingId = 5L;
        boolean isApproved = true;
        UserShort user = new UserShort(userId, "name");
        ItemForBooking item = new ItemForBooking(itemId, "name");
        BookingResponseDto bookingResponseDto = new BookingResponseDto(5L, startDataTime, endDataTime, user, item, StatusBooking.APPROVED);

        when(bookingService.updateStatusBooking(userId, bookingId, isApproved)).thenReturn(bookingResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/5")
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingResponseDto)));
    }

    @SneakyThrows
    @Test
    void findBooking() {
        long userId = 1L;
        long itemId = 2L;
        long bookingId = 5L;
        UserShort user = new UserShort(userId, "name");
        ItemForBooking item = new ItemForBooking(itemId, "name");
        BookingResponseDto bookingResponseDto = new BookingResponseDto(5L, startDataTime, endDataTime, user, item, StatusBooking.APPROVED);

        when(bookingService.findBooking(userId, bookingId)).thenReturn(bookingResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/5")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookingResponseDto)));
    }

    @SneakyThrows
    @Test
    void findBookingsByUser() {
        long userId = 1L;
        long itemId = 2L;
        String state = "ALL";
        int from = 0;
        int size = 10;
        UserShort user = new UserShort(userId, "name");
        ItemForBooking item = new ItemForBooking(itemId, "name");
        BookingResponseDto bookingResponseDto = new BookingResponseDto(5L, startDataTime, endDataTime, user, item, StatusBooking.APPROVED);
        List<BookingResponseDto> bookings = List.of(bookingResponseDto);

        when(bookingService.findBookingsByUser(userId, state, from, size)).thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookings)));
    }

    @SneakyThrows
    @Test
    void findAllBookingsByItemsOwner() {
        long userId = 1L;
        long itemId = 2L;
        String state = "ALL";
        int from = 0;
        int size = 10;
        UserShort user = new UserShort(userId, "name");
        ItemForBooking item = new ItemForBooking(itemId, "name");
        BookingResponseDto bookingResponseDto = new BookingResponseDto(5L, startDataTime, endDataTime, user, item, StatusBooking.APPROVED);
        List<BookingResponseDto> bookings = List.of(bookingResponseDto);

        when(bookingService.findAllBookingsByItemsOwner(userId, state, from, size)).thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookings)));
    }
}