package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

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
}