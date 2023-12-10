package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    RequestRepository requestRepository;

    @Test
    void findItemByOwnerId() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "name", "description", true));
        Item itemSecond = itemRepository.save(new Item(null, userFirst, "name second", "description second", true));
        User userSecond = userRepository.save(new User(null, "name", "new email@"));
        Item itemThird = itemRepository.save(new Item(null, userSecond, "name second", "description second", true));
        Pageable pageable = PageRequest.of(0, 10);

        Page<Item> findItem = itemRepository.findItemByOwnerId(userFirst.getId(), pageable);

        assertEquals(findItem.getTotalElements(), 2);
    }

    @Test
    void searchByNameAndDescriptionAndAvailable() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        Item itemSecond = itemRepository.save(new Item(null, userFirst, "name second", "description second", true));
        User userSecond = userRepository.save(new User(null, "name", "new email@"));
        Item itemThird = itemRepository.save(new Item(null, userFirst, "name second", "Вещица", true));
        Pageable pageable = PageRequest.of(0, 10);
        String text = "вещ";

        Page<Item> findItem = itemRepository.searchByNameAndDescriptionAndAvailable(text, pageable);

        assertEquals(findItem.getTotalElements(), 2);
    }

    @Test
    void existsItemByIdAndAvailableIsTrue_whenItemExistAndAvailableIsTrue_thenReturnTrue() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        Item itemSecond = itemRepository.save(new Item(null, userFirst, "name second", "description second", false));

        Boolean response = itemRepository.existsItemByIdAndAvailableIsTrue(itemFirst.getId());

        assertEquals(response, true);
    }

    @Test
    void existsItemByIdAndAvailableIsTrue_whenItemNonExist_thenReturnFalse() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        Item itemSecond = itemRepository.save(new Item(null, userFirst, "name second", "description second", false));

        Boolean response = itemRepository.existsItemByIdAndAvailableIsTrue(3L);

        assertEquals(response, false);
    }

    @Test
    void existsItemByIdAndAvailableIsTrue_whenItemExistAndAvailableIsFalse_thenReturnFalse() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        Item itemSecond = itemRepository.save(new Item(null, userFirst, "name second", "description second", false));

        Boolean response = itemRepository.existsItemByIdAndAvailableIsTrue(itemSecond.getId());

        assertEquals(response, false);
    }

    @Test
    void existsItemByIdAndOwnerId_whenItemByOwner_thenReturnTrue() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        Item itemSecond = itemRepository.save(new Item(null, userFirst, "name second", "description second", true));
        User userSecond = userRepository.save(new User(null, "name", "new email@"));
        Item itemThird = itemRepository.save(new Item(null, userFirst, "name second", "Вещица", true));

        Boolean response = itemRepository.existsItemByIdAndOwnerId(itemFirst.getId(), userFirst.getId());

        assertEquals(response, true);
    }

    @Test
    void existsItemByIdAndOwnerId_whenItemNonByOwner_thenReturnFalse() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "вещь", "description", true));
        Item itemSecond = itemRepository.save(new Item(null, userFirst, "name second", "description second", true));
        User userSecond = userRepository.save(new User(null, "name", "new email@"));
        Item itemThird = itemRepository.save(new Item(null, userFirst, "name second", "Вещица", true));

        Boolean response = itemRepository.existsItemByIdAndOwnerId(itemFirst.getId(), userSecond.getId());

        assertEquals(response, false);
    }

    @Test
    void findAllByItemRequest_id() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        User userSecond = userRepository.save(new User(null, "name", "new email@"));
        ItemRequest itemRequest = requestRepository.save(new ItemRequest(null, userFirst, "description", null));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "вещь", "description", true, itemRequest));
        Item itemSecond = itemRepository.save(new Item(null, userFirst, "name second", "description second", true, itemRequest));
        Item itemThird = itemRepository.save(new Item(null, userFirst, "name second", "Вещица", true));

        List<Item> response = itemRepository.findAllByItemRequest_id(itemRequest.getId());

        assertEquals(response.size(), 2);
    }
}