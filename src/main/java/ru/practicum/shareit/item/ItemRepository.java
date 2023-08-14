package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwnerOrderById(User owner, Pageable pageable);

    @Query(" select i from Item i " +
            "where lower(i.name) like lower(concat('%', :search, '%')) " +
            " or lower(i.description) like lower(concat('%', :search, '%')) " +
            " and i.available = true")
    Page<Item> getItemsBySearchQuery(@Param("search") String text, Pageable pageable);

    List<Item> findByRequestId(Long requestId);

    @Query("SELECT i.owner " +
            "FROM Item i " +
            "WHERE i.id = :id")
    User getOwnerById(@Param("id") Long id);
}
