package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private ItemMapper itemMapper;

    @Test
    void toItemDto_whenRequestIdNull_returnItemDto() {
        User user = new User(1L, "name", "email@");
        Item item = new Item(2L, user, "item name", "description", true, null);
        Comment comment = new Comment(3L, "text", item, user, LocalDateTime.now());

        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        ItemDto itemDto = itemMapper.toItemDto(item);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getOwnerId(), user.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertNull(itemDto.getRequestId());
        assertEquals(itemDto.getComments().size(), 1);
    }

    @Test
    void toItemDto_whenRequestIdNotNull_returnItemDtoWithRequestId() {
        User user = new User(1L, "name", "email@");
        ItemRequest itemRequest = new ItemRequest(4L, new User(), "description", LocalDateTime.now());
        Item item = new Item(2L, user, "item name", "description", true, itemRequest);
        Comment comment = new Comment(3L, "text", item, user, LocalDateTime.now());


        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        ItemDto itemDto = itemMapper.toItemDto(item);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getOwnerId(), user.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
        assertNotNull(itemDto.getRequestId());
        assertEquals(itemDto.getComments().size(), 1);
    }

    @Test
    void toItem_whenItemDtoWithRequestId_returnItem() {
        ItemDto itemDto = new ItemDto(1L, 2L, "name", "description", true, 4L, new ArrayList<>());

        Item response = itemMapper.toItem(itemDto);
        assertEquals(itemDto.getId(), response.getId());
        assertEquals(itemDto.getOwnerId(), response.getOwner().getId());
        assertEquals(itemDto.getName(), response.getName());
        assertEquals(itemDto.getDescription(), response.getDescription());
        assertEquals(itemDto.getAvailable(), response.getAvailable());
        assertEquals(itemDto.getRequestId(), response.getItemRequest().getId());
    }

    @Test
    void toItem_whenItemDtoWithoutRequestId_returnItem() {
        ItemDto itemDto = new ItemDto(1L, 2L, "name", "description", true, null, new ArrayList<>());

        Item response = itemMapper.toItem(itemDto);
        assertEquals(itemDto.getId(), response.getId());
        assertEquals(itemDto.getOwnerId(), response.getOwner().getId());
        assertEquals(itemDto.getName(), response.getName());
        assertEquals(itemDto.getDescription(), response.getDescription());
        assertEquals(itemDto.getAvailable(), response.getAvailable());
        assertNull(response.getItemRequest());
    }

    @Test
    void toItemForOwnerByItemDto_whenIsOwnerTrue_thenReturnItemWithLastBookingAndNextBooking() {
        boolean isOwner = true;
        User user = new User(1L, "name", "email@");
        ItemRequest itemRequest = new ItemRequest(4L, new User(), "description", LocalDateTime.now());
        Item item = new Item(2L, user, "item name", "description", true, itemRequest);
        Comment comment = new Comment(3L, "text", item, user, LocalDateTime.now());
        Booking lastBooking = new Booking(5L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(5), user, item, StatusBooking.APPROVED);
        Booking nextBooking = new Booking(6L, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(3), user, item, StatusBooking.APPROVED);

        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findBookingsByItem(item)).thenReturn(List.of(lastBooking, nextBooking));

        ItemWithBookingsDto response = itemMapper.toItemForOwnerByItemDto(item, isOwner);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getOwner().getId(), response.getOwnerId());
        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getAvailable(), response.getAvailable());
        assertEquals(item.getItemRequest().getId(), response.getRequestId());
        assertNotNull(response.getComments());
        assertEquals(lastBooking.getId(), response.getLastBooking().getId());
        assertEquals(nextBooking.getId(), response.getNextBooking().getId());

    }

    @Test
    void toItemForOwnerByItemDto_whenNextBookingNotExist_thenReturnItemWithoutLastBooking() {
        boolean isOwner = true;
        User user = new User(1L, "name", "email@");
        ItemRequest itemRequest = new ItemRequest(4L, new User(), "description", LocalDateTime.now());
        Item item = new Item(2L, user, "item name", "description", true, itemRequest);
        Comment comment = new Comment(3L, "text", item, user, LocalDateTime.now());
        Booking lastBooking = new Booking(5L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(5), user, item, StatusBooking.APPROVED);

        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findBookingsByItem(item)).thenReturn(List.of(lastBooking));

        ItemWithBookingsDto response = itemMapper.toItemForOwnerByItemDto(item, isOwner);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getOwner().getId(), response.getOwnerId());
        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getAvailable(), response.getAvailable());
        assertEquals(item.getItemRequest().getId(), response.getRequestId());
        assertNotNull(response.getComments());
        assertEquals(lastBooking.getId(), response.getLastBooking().getId());
        assertNull(response.getNextBooking());
    }

    @Test
    void toItemForOwnerByItemDto_whenLastBookingNotExist_thenReturnItemWithoutNextBooking() {
        boolean isOwner = true;
        User user = new User(1L, "name", "email@");
        ItemRequest itemRequest = new ItemRequest(4L, new User(), "description", LocalDateTime.now());
        Item item = new Item(2L, user, "item name", "description", true, itemRequest);
        Comment comment = new Comment(3L, "text", item, user, LocalDateTime.now());
        Booking nextBooking = new Booking(6L, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(3), user, item, StatusBooking.APPROVED);

        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findBookingsByItem(item)).thenReturn(List.of(nextBooking));

        ItemWithBookingsDto response = itemMapper.toItemForOwnerByItemDto(item, isOwner);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getOwner().getId(), response.getOwnerId());
        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getAvailable(), response.getAvailable());
        assertEquals(item.getItemRequest().getId(), response.getRequestId());
        assertNotNull(response.getComments());
        assertNull(response.getLastBooking());
        assertEquals(nextBooking.getId(), response.getNextBooking().getId());
    }

    @Test
    void toItemForRequest_whenItemWithRequest_thenReturnItemWithRequestId() {
        User user = new User(1L, "name", "email@");
        ItemRequest itemRequest = new ItemRequest(4L, new User(), "description", LocalDateTime.now());
        Item item = new Item(2L, user, "item name", "description", true, itemRequest);

        ItemForRequest response = itemMapper.toItemForRequest(item);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getAvailable(), response.isAvailable());
        assertEquals(item.getItemRequest().getId(), response.getRequestId());
    }

    @Test
    void toItemForRequest_whenItemWithoutRequest_thenReturnItemWithoutRequestId() {
        User user = new User(1L, "name", "email@");
        Item item = new Item(2L, user, "item name", "description", true, null);

        ItemForRequest response = itemMapper.toItemForRequest(item);
        assertEquals(item.getId(), response.getId());
        assertEquals(item.getName(), response.getName());
        assertEquals(item.getDescription(), response.getDescription());
        assertEquals(item.getAvailable(), response.isAvailable());
        assertNull(response.getRequestId());
    }
}