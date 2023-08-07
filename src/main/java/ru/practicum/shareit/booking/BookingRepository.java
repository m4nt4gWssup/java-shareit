package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker(User booker);

    List<Booking> findByBookerAndStatus(User booker, Status status);

    List<Booking> findByItemIn(List<Item> item);

    List<Booking> findByItemInAndStatus(List<Item> item, Status status);

    Booking findFirstByItemAndStatusAndStartAfterOrderByStart(Item item, Status status, LocalDateTime now);

    Booking findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(Item item, Status status, LocalDateTime now);

    List<Booking> findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
            List<Item> items, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(
            List<Item> items, Status approved, LocalDateTime now);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(Long itemId, Long userId,
                                                                  LocalDateTime end, Status status);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 " +
            "and b.start < current_timestamp and b.end > current_timestamp  " +
            "order by b.start desc")
    List<Booking> getCurrentByUserId(Long id);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 " +
            "and b.end < current_timestamp " +
            "order by b.start desc")
    List<Booking> getBookingByUserIdAndFinishAfterNow(Long userId);
}
