package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrItemOwnerId(Long bookerId, Long ownerId);

    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId);

    List<Booking> findByItemOwnerId(Long ownerId);

    @Query("select b from Booking b " +
            "where b.id= :bookingId AND (b.booker.id = :userId OR b.item.owner.id = :userId)")
    Optional<Booking> findByIdAndUserId(@Param("bookingId") Long bookingId,
                                        @Param("userId") Long userId);
    //поиск вещи по id бронирования и id booker или хозяина

    @Query("SELECT b FROM Booking b WHERE (b.booker.id = :userId OR b.item.owner.id = :userId) " +
            "AND b.start < :currentDate AND b.end > :currentDate " +
            "ORDER BY b.start DESC")
    List<Booking> findByUserIdCurrentBook(@Param("userId") Long userId,  //текущие бронирования
                                          @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE (b.booker.id = :userId OR b.item.owner.id = :userId) " +
            "AND b.end < :currentDate " +
            "ORDER BY b.start DESC")
    List<Booking> findByUserIdPastBook(@Param("userId") Long userId,  //прошедшие бронирования
                                       @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE (b.booker.id = :userId OR b.item.owner.id = :userId) " +
            "AND b.start > :currentDate " +
            "ORDER BY b.start DESC")
    List<Booking> findByUserIdFutureBook(@Param("userId") Long userId,  //будущие бронирования
                                         @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT b FROM Booking b WHERE (b.booker.id = :userId OR b.item.owner.id = :userId) " +
            "AND b.status = :status " +
            "ORDER BY b.start DESC")
    List<Booking> findByUserIdAndStatus(@Param("userId") Long userId,  //поиск со статусом
                                        @Param("status") Status status);


    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.start < :currentDate AND b.end > :currentDate ")
    Optional<Booking> findByItemIdCurrentBook(@Param("itemId") Long itemId,  //текущие бронирования для вещи
                                              @Param("currentDate") LocalDateTime currentDate);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime date); //поиск по id вещи и его ближайшее будущее бронирование
}
