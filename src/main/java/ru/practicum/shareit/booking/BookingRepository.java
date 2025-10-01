package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findBookingById(Long id);

    boolean existsByItem_IdAndStatusInAndEndAfterAndStartBefore(
            Long itemId,
            List<BookingStatus> statuses,
            LocalDateTime start,
            LocalDateTime end
    );

    boolean existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
            Long bookerId,
            Long itemId,
            BookingStatus status,
            LocalDateTime before
    );


    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "order by b.start desc")
    List<Booking> findAllByBookerOrderByStartDesc(@Param("bookerId") Long bookerId);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.start < :now and b.end > :now " +
            "order by b.start desc")
    List<Booking> findCurrentByBooker(@Param("bookerId") Long bookerId,
                                      @Param("now") LocalDateTime now);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.end < :now " +
            "order by b.start desc")
    List<Booking> findPastByBooker(@Param("bookerId") Long bookerId,
                                   @Param("now") LocalDateTime now);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.start > :now " +
            "order by b.start desc")
    List<Booking> findFutureByBooker(@Param("bookerId") Long bookerId,
                                     @Param("now") LocalDateTime now);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.booker.id = :bookerId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<Booking> findByBookerAndStatus(@Param("bookerId") Long bookerId,
                                        @Param("status") BookingStatus status);


    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "order by b.start desc")
    List<Booking> findAllByOwnerOrderByStartDesc(@Param("ownerId") Long ownerId);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "and b.start < :now and b.end > :now " +
            "order by b.start desc")
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId,
                                     @Param("now") LocalDateTime now);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "and b.end < :now " +
            "order by b.start desc")
    List<Booking> findPastByOwner(@Param("ownerId") Long ownerId,
                                  @Param("now") LocalDateTime now);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "and b.start > :now " +
            "order by b.start desc")
    List<Booking> findFutureByOwner(@Param("ownerId") Long ownerId,
                                    @Param("now") LocalDateTime now);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("select b from Booking b " +
            "where b.item.owner.id = :ownerId " +
            "and b.status = :status " +
            "order by b.start desc")
    List<Booking> findByOwnerAndStatus(@Param("ownerId") Long ownerId,
                                       @Param("status") BookingStatus status);

    Optional<Booking> findFirstByItem_IdAndStartBeforeAndStatusOrderByStartDesc(
            Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findFirstByItem_IdAndStartAfterAndStatusOrderByStartAsc(
            Long itemId, LocalDateTime now, BookingStatus status);

    Optional<Booking> findTopByItem_Owner_IdAndStatusOrderByIdDesc(
            Long ownerId, BookingStatus status);

    @Query("""
            select b from Booking b
            where b.item.owner.id = :ownerId
              and b.status = :status
            order by b.id desc
            """)
    Optional<Booking> findLatestByOwnerAndStatus(@Param("ownerId") Long ownerId,
                                                 @Param("status") BookingStatus status);

    @Query(value = """
            SELECT b.*
            FROM bookings b
            JOIN items i ON i.id = b.item_id
            WHERE i.owner_id = :ownerId
              AND b.status = :status
            ORDER BY b.id DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<Booking> findLatestByOwnerAndStatusNative(@Param("ownerId") Long ownerId,
                                                       @Param("status") String status);
}

