package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long userId);

    @Query("select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available = true")
    List<Item> findByNameContaining(String text);

    @Query("SELECT i, bLast, bNext, c FROM Item i " +
            "LEFT JOIN Booking bLast ON bLast.item.id = " +
            "i.id AND bLast.end < CURRENT_TIMESTAMP " +
            "LEFT JOIN Booking bNext ON bNext.item.id = " +
            "i.id AND bNext.start > CURRENT_TIMESTAMP " +
            "LEFT JOIN Comment c ON c.item.id = i.id " +
            "WHERE i.owner.id = :userId")
    List<Object[]> findItemsWithBookingsAndCommentsByUserId(@Param("userId") Long userId);

    List<Item> findByRequestId(Long id);
}
