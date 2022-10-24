package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdOrderByStartDesc(int id, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(int userId,
                                                                                 LocalDateTime nowStart,
                                                                                 LocalDateTime nowEnd);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(int userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(int userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int userId, BookingStatus status);

    @Query("select b from Booking b where b.item.owner.id = ?1 and " +
            "b.start <= ?2 and b.end >= ?3 order by b.start desc")
    List<Booking> findAllCurrentByItemsOwnerId(int userId, LocalDateTime nowStart, LocalDateTime nowEnd);

    @Query("select b from Booking b where b.item.owner.id = ?1 and " +
            "b.end <= ?2 order by b.start desc")
    List<Booking> findAllPastByItemsOwnerId(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = ?1 and " +
            "b.start >= ?2 order by b.start desc")
    List<Booking> findAllFutureByItemsOwnerId(int userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.owner.id = ?1 and " +
            "b.status = ?2 order by b.start desc")
    List<Booking> findAllStatusByItemsOwnerId(int userId, BookingStatus status);

    @Query("select b from Booking b where b.item.id = ?1")
    List<Booking> findAllByItemsId(int itemId);

    @Query("select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    List<Booking> findAllByItemsOwnerId(int userId, Pageable pageable);

    @Query("select b from Booking b where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and b.start <= ?3 order by b.end desc ")
    List<Booking> findLastBookingByItemId(int itemId, int userId, LocalDateTime now);

    @Query("select b from Booking b where b.item.id = ?1 and " +
            "b.item.owner.id = ?2 and b.start >= ?3 order by b.start asc")
    List<Booking> findNextBookingByItemId(int itemId, int userId, LocalDateTime now);

    @Query("select (count(b) > 0) from Booking b where b.item.id = ?1 and b.booker.id = ?2 and b.end < ?3")
    boolean existsByItemIdAndBookerIdAndEndBefore(int itemId, int userId, LocalDateTime now);
}
