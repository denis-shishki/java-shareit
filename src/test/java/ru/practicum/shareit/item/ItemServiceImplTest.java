package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.constants.StatusBooking;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemMapper itemMapper;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    public void updateItem_whenItemWithNewNameDescriptionAvailable_thenReturnItem() {
        long itemId = 1L;
        long userId = 2L;
        ItemDto itemRequest = new ItemDto("new name", "new description", true);
        Item updateItem = new Item("new name", "new description", true);
        Item oldItem = new Item(itemId, "name", "description", false);
        Item modificItem = new Item(itemId, "new name", "new description", true);
        ItemDto responseItem = new ItemDto(itemId, "new name", "new description", true);

        when(itemRepository.existsItemByIdAndOwnerId(itemId, userId)).thenReturn(true);
        when(itemMapper.toItem(itemRequest)).thenReturn(updateItem);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(modificItem)).thenReturn(modificItem);
        when(itemMapper.toItemDto(modificItem)).thenReturn(responseItem);

        ItemDto checkResponseItem = itemService.updateItem(itemRequest, userId, itemId);

        assertEquals(checkResponseItem, responseItem);
    }

    @Test
    public void updateItem_whenItemWithNewNameDescription_thenReturnItem() {
        long itemId = 1L;
        long userId = 2L;
        ItemDto itemRequest = new ItemDto("new name", "new description", null);
        Item updateItem = new Item("new name", "new description", null);
        Item oldItem = new Item(itemId, "name", "description", false);
        Item modificItem = new Item(itemId, "new name", "new description", false);
        ItemDto responseItem = new ItemDto(itemId, "new name", "new description", false);

        when(itemRepository.existsItemByIdAndOwnerId(itemId, userId)).thenReturn(true);
        when(itemMapper.toItem(itemRequest)).thenReturn(updateItem);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(modificItem)).thenReturn(modificItem);
        when(itemMapper.toItemDto(modificItem)).thenReturn(responseItem);

        ItemDto checkResponseItem = itemService.updateItem(itemRequest, userId, itemId);

        assertEquals(checkResponseItem, responseItem);
    }

    @Test
    public void updateItem_whenItemWithNewName_thenItemNotFoundExceptionThrow() {
        long itemId = 1L;
        long userId = 2L;
        ItemDto itemRequest = new ItemDto("new name", null, null);
        Item updateItem = new Item("new name", null, null);
        Item oldItem = new Item(itemId, "name", "description", false);
        Item modificItem = new Item(itemId, "new name", "description", false);
        ItemDto responseItem = new ItemDto(itemId, "new name", "description", false);

        when(itemRepository.existsItemByIdAndOwnerId(itemId, userId)).thenReturn(true);
        when(itemMapper.toItem(itemRequest)).thenReturn(updateItem);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(oldItem));
        when(itemRepository.save(modificItem)).thenReturn(modificItem);
        when(itemMapper.toItemDto(modificItem)).thenReturn(responseItem);

        ItemDto checkResponseItem = itemService.updateItem(itemRequest, userId, itemId);
        assertEquals(checkResponseItem, responseItem);
    }

    @Test
    public void updateItem_whenUserIdNonExist_thenUserNotFoundExceptionThrow() {
        long userId = 0L;
        long itemId = 1L;

        doThrow(NotFoundException.class).when(userService).checkExistUser(userId);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(new ItemDto(), userId, itemId));
        verify(itemRepository, never()).save(Mockito.any(Item.class));
    }

    @Test
    public void updateItem_whenItemMissingFromUser_thenItemNotFoundExceptionThrow() {
        long userId = 0L;
        long itemId = 1L;

        when(itemRepository.existsItemByIdAndOwnerId(itemId, userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.updateItem(new ItemDto(), userId, itemId));
        verify(itemRepository, never()).save(Mockito.any(Item.class));
    }

    @Test
    public void createItem_whenItemValid_thenReturnItem() {
        long userId = 1L;
        String name = "name";
        String description = "description";
        boolean available = true;
        ItemDto itemDto = new ItemDto(name, description, available);
        Item item = new Item(itemDto.getId(),
                new User(userId),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable());

        when(itemMapper.toItem(Mockito.any(ItemDto.class))).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        ItemDto response = itemService.createItem(itemDto, userId);
        Mockito.verify(itemRepository, Mockito.times(1)).save(item);
        assertEquals(itemDto.getName(), response.getName());
        assertEquals(itemDto.getOwnerId(), response.getOwnerId());
        assertEquals(itemDto.getAvailable(), response.getAvailable());
    }

    @Test
    public void createItem_whenItemWithoutName_thenValidationException() {
        long userId = 1L;
        String description = "description";
        boolean available = true;
        ItemDto itemDto = new ItemDto(null, description, available);

        assertThrows(ValidationException.class, () -> itemService.createItem(itemDto, userId));
        verify(itemRepository, never()).save(Mockito.any(Item.class));
    }

    @Test
    public void createItem_whenItemWithoutDescription_thenValidationException() {
        long userId = 1L;
        String name = "name";
        boolean available = true;
        ItemDto itemDto = new ItemDto(name, null, available);

        assertThrows(ValidationException.class, () -> itemService.createItem(itemDto, userId));
        verify(itemRepository, never()).save(Mockito.any(Item.class));
    }

    @Test
    public void createItem_whenItemWithoutAvailable_thenValidationException() {
        long userId = 1L;
        String name = "name";
        String description = "description";
        ItemDto itemDto = new ItemDto(name, description, null);

        assertThrows(ValidationException.class, () -> itemService.createItem(itemDto, userId));
        verify(itemRepository, never()).save(Mockito.any(Item.class));
    }

    @Test
    public void findItem_whenItemNotExist_thenNotFoundException() {
        long itemId = 1L;
        long userId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItem(itemId, userId));
        verify(itemMapper, never()).toItemForOwnerByItemDto(Mockito.any(Item.class), anyBoolean());
    }

    @Test
    public void findItem_whenItemExist_thenReturnItem() {
        long itemId = 1L;
        long userId = 2L;
        Item item = new Item(itemId, new User(3L), "name", "description", true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemForOwnerByItemDto(item, false)).thenReturn(new ItemWithBookingsDto());

        ItemWithBookingsDto response = itemService.findItem(itemId, userId);
        assertNotNull(response);

    }

    @Test
    public void findItem_whenItemByOwner_thenReturnItem() {
        long itemId = 1L;
        long userId = 2L;
        Item item = new Item(itemId, new User(userId), "name", "description", true);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemForOwnerByItemDto(item, true)).thenReturn(new ItemWithBookingsDto());

        ItemWithBookingsDto response = itemService.findItem(itemId, userId);
        assertNotNull(response);
    }

    @Test
    public void findAllItemForOwner_whenUserExist_thenReturnItems() {
        long userId = 1L;
        int from = 0;
        int size = 10;
        Pageable pageable = Paginator.getPageable(from, size);
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item());
        itemList.add(new Item());
        itemList.add(new Item());
        Page<Item> itemPage = new PageImpl<>(itemList, PageRequest.of(0, 10), itemList.size());

        when(itemRepository.findItemByOwnerId(userId, pageable)).thenReturn(itemPage);
        when(itemMapper.toItemForOwnerByItemDto(Mockito.any(Item.class), anyBoolean())).thenReturn(new ItemWithBookingsDto());

        List<ItemWithBookingsDto> response = itemService.findAllItemForOwner(userId, from, size);

        assertEquals(response.size(), itemList.size());
    }

    @Test
    public void searchAvailableItem() {
        int from = 0;
        int size = 10;
        Pageable pageable = Paginator.getPageable(from, size);
        String text = "text";
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item());
        itemList.add(new Item());
        itemList.add(new Item());
        Page<Item> itemPage = new PageImpl<>(itemList, PageRequest.of(0, 10), itemList.size());

        when(itemMapper.toItemDto(Mockito.any(Item.class))).thenReturn(new ItemDto());
        when(itemRepository.searchByNameAndDescriptionAndAvailable(text, pageable)).thenReturn(itemPage);

        List<ItemDto> response = itemService.searchAvailableItem(text, from, size);
        assertEquals(itemList.size(), response.size());
    }

    @Test
    public void checkExistItem_whenUserExist() {
        long itemId = 1L;

        when(itemRepository.existsById(itemId)).thenReturn(true);

        itemService.checkExistItem(itemId);
        assertTrue(true);
    }

    @Test
    public void checkExistItem_whenUserNotExist_returnNotFoundException() {
        long itemId = 1L;

        when(itemRepository.existsById(itemId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.checkExistItem(itemId));
    }

    @Test
    public void findItem_whenItemExist_returnItem() {
        long itemId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(itemMapper.toItemForOwnerByItemDto(Mockito.any(Item.class), anyBoolean())).thenReturn(new ItemWithBookingsDto());

        ItemWithBookingsDto response = itemService.findItem(itemId);
        assertNotNull(response);
    }

    @Test
    public void findItem_whenItemNotExist_returnNotFoundException() {
        long itemId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.findItem(itemId));
    }

    @Test
    public void createComment_whenCommentValid_returnComment() {
        long itemId = 1L;
        long userId = 2L;
        User user = new User(userId, "name", "email@");
        Booking booking = new Booking(LocalDateTime.MIN, StatusBooking.APPROVED);
        CommentDto commentRequest = new CommentDto("text");
        Comment comment = new Comment(2L, "text", new Item(), user, LocalDateTime.now());

        when(itemRepository.existsById(itemId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemIdAndBookerId(itemId, userId)).thenReturn(List.of(booking));
        when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);

        CommentDto response = itemService.createComment(commentRequest, userId, itemId);
        assertNotNull(response);
    }

    @Test
    public void createComment_whenCommentNotValid_returnValidationException() {
        long itemId = 1L;
        long userId = 2L;
        CommentDto commentRequest = new CommentDto();

        assertThrows(ValidationException.class, () -> itemService.createComment(commentRequest, userId, itemId));
        verify(commentRepository, never()).save(Mockito.any(Comment.class));
    }

    @Test
    public void findItemForRequest() {
        long requestId = 1L;
        Item item = new Item(2L, "name", "description", true);
        List<Item> items = List.of(item);

        when(itemRepository.findAllByItemRequest_id(requestId)).thenReturn(items);
        when(itemMapper.toItemForRequest(item)).thenReturn(Mockito.any(ItemForRequest.class));

        List<ItemForRequest> response = itemService.findItemForRequest(requestId);
        assertNotNull(response);
        assertEquals(1, response.size());
    }
}