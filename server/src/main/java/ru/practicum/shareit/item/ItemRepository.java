package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemByOwnerId(long ownerId, Pageable pageable);

    @Query(" select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))" +
            "and i.available is true")
    List<Item> searchByNameAndDescriptionAndAvailable(String text, Pageable pageable);

    boolean existsItemByIdAndAvailableIsTrue(long itemId);

    boolean existsItemByIdAndOwnerId(long itemId, long userId);

    List<Item> findAllByItemRequest_id(long requestId);

}
