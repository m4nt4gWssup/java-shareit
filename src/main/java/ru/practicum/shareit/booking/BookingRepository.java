package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBooker(User booker, Pageable pageable);

    Page<Booking> findByBookerAndStatus(User booker, Status status, Pageable pageable);

    Page<Booking> findByItemIn(List<Item> item, Pageable pageable);

    Page<Booking> findByItemInAndStatus(List<Item> item, Status status, Pageable pageable);

    Booking findFirstByItemAndStatusAndStartAfterOrderByStart(Item item, Status status, LocalDateTime now);

    Booking findFirstByItemAndStatusAndStartBeforeOrderByStartDesc(Item item, Status status, LocalDateTime now);

    Page<Booking> findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
            List<Item> items, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Booking> findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(
            List<Item> items, Status approved, LocalDateTime now, Pageable pageable);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(Long itemId, Long userId,
                                                                  LocalDateTime end, Status status);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 " +
            "and b.start < current_timestamp and b.end > current_timestamp  " +
            "order by b.start desc")
    Page<Booking> getCurrentByUserId(Long id, Pageable pageable);

    @Query("select b " +
            "from Booking as b " +
            "join b.booker as u " +
            "where u.id = ?1 " +
            "and b.end < current_timestamp " +
            "order by b.start desc")
    Page<Booking> getBookingByUserIdAndFinishAfterNow(Long userId, Pageable pageable);
}
