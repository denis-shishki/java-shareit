package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByBookerIdIs(long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.owner.id = ?1")
    Page<Booking> findAllBookingsByItemsOwner(long userId, Pageable pageable);

    List<Booking> findBookingsByItem(Item item);

    List<Booking> findBookingByItemIdAndBookerId(long itemId, long userId);

}
