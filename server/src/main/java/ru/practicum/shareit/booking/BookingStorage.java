//package ru.practicum.shareit.booking;
//
//import org.springframework.stereotype.Component;
//import ru.practicum.shareit.booking.model.Booking;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class BookingStorage {
//    private final Map<Long, Booking> bookings = new HashMap<Long, Booking>();
//    private int id = 0;
//
//    public Booking createBooking(Booking booking) {
//        //booking.setId(id++);
//        bookings.put(booking.getId(), booking);
//        return booking;
//    }
//
//    public Booking findBooking(int bookingId) {
//        return bookings.get(bookingId);
//    }
//
//    public Booking updateBooking(Booking booking) {
//        bookings.put(booking.getId(), booking);
//        return bookings.get(booking.getId());
//    }
//
//    public void deleteBooking(int bookingId) {
//        bookings.remove(bookingId);
//    }
//}
