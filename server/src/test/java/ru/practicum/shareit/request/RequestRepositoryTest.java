package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findAllByRequestor_IdIs() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "name", "description", true));
        ItemRequest itemRequestFirst = requestRepository.save(new ItemRequest(null, userFirst, "description", LocalDateTime.now().minusDays(1)));
        ItemRequest itemRequestSecond = requestRepository.save(new ItemRequest(null, userFirst, "description", LocalDateTime.now()));

        List<ItemRequest> response = requestRepository.findAllByRequestor_IdIs(userFirst.getId());

        assertEquals(response.size(), 2);
    }

    @Test
    void findAllBySort() {
        User userFirst = userRepository.save(new User(null, "name", "email@"));
        User userSecond = userRepository.save(new User(null, "name", "new email@"));
        Item itemFirst = itemRepository.save(new Item(null, userFirst, "name", "description", true));
        ItemRequest itemRequestFirst = requestRepository.save(new ItemRequest(null, userFirst, "description", LocalDateTime.now().minusDays(1)));
        ItemRequest itemRequestSecond = requestRepository.save(new ItemRequest(null, userFirst, "description", LocalDateTime.now()));
        ItemRequest itemRequestThird = requestRepository.save(new ItemRequest(null, userSecond, "description", LocalDateTime.now()));
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));

        Page<ItemRequest> response = requestRepository.findAllNonOwnerBySort(userSecond.getId(), pageable);

        assertEquals(response.getTotalElements(), 2);
        assertEquals(response.toList().get(0), itemRequestSecond);
        assertEquals(response.toList().get(1), itemRequestFirst);
    }
}