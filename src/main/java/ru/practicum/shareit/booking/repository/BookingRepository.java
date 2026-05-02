package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Находит все бронирования пользователя (как booker'а),
     * отсортированные по дате начала в порядке убывания.
     *
     * @param bookerId идентификатор пользователя, который арендует вещь
     * @return список бронирований пользователя
     */
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    /**
     * Находит будущие бронирования пользователя (дата начала после текущего момента).
     * Результат сортируется по дате начала в порядке убывания.
     *
     * @param bookerId идентификатор пользователя
     * @param now текущая дата и время
     * @return список будущих бронирований
     */
    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    /**
     * Находит завершенные (прошлые) бронирования пользователя.
     * Результат сортируется по дате начала в порядке убывания.
     *
     * @param bookerId идентификатор пользователя
     * @param now текущая дата и время
     * @return список завершенных бронирований
     */
    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    /**
     * Находит текущие бронирования пользователя (дата начала до текущего момента,
     * дата окончания после текущего момента).
     * Результат сортируется по дате начала в порядке убывания.
     *
     * @param bookerId идентификатор пользователя
     * @param start дата начала бронирования (должна быть до текущего момента)
     * @param end дата окончания бронирования (должна быть после текущего момента)
     * @return список текущих бронирований
     */
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    /**
     * Находит бронирования пользователя по статусу.
     * Результат сортируется по дате начала в порядке убывания.
     *
     * @param bookerId идентификатор пользователя
     * @param status статус бронирования (WAITING, APPROVED, REJECTED, CANCELED)
     * @return список бронирований с указанным статусом
     */
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    /**
     * Находит все бронирования для всех вещей владельца.
     * Результат сортируется по дате начала в порядке убывания.
     *
     * @param userId идентификатор владельца вещей
     * @return список бронирований всех вещей владельца
     */
    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

    /**
     * Находит текущие бронирования для всех вещей владельца.
     *
     * @param userId идентификатор владельца
     * @param now текущая дата и время
     * @param end дата окончания (должна быть после текущего момента)
     * @return список текущих бронирований вещей владельца
     */
    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now,
                                                                                LocalDateTime end);

    /**
     * Находит завершенные бронирования для всех вещей владельца.
     *
     * @param userId идентификатор владельца
     * @param now текущая дата и время
     * @return список завершенных бронирований вещей владельца
     */
    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    /**
     * Находит будущие бронирования для всех вещей владельца.
     *
     * @param userId идентификатор владельца
     * @param now текущая дата и время
     * @return список будущих бронирований вещей владельца
     */
    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    /**
     * Находит бронирования по статусу для всех вещей владельца.
     *
     * @param userId идентификатор владельца
     * @param status статус бронирования
     * @return список бронирований с указанным статусом
     */
    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, Status status);

    /**
     * Проверяет, есть ли у пользователя завершенные подтвержденные бронирования
     * для указанной вещи. Используется для проверки права на оставление отзыва.
     *
     * @param userId идентификатор пользователя, который хочет оставить отзыв
     * @param itemId идентификатор вещи
     * @param status статус бронирования
     * @param now текущая дата и время
     * @return true, если есть завершенное подтвержденное бронирование, иначе false
     */
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.end < :now")
    boolean existsCompletedBooking(@Param("userId") Long userId,
                                   @Param("itemId") Long itemId,
                                   @Param("status") Status status,
                                   @Param("now") LocalDateTime now);


    /**
     * Находит последнее завершенное бронирование для указанной вещи.
     * Используется для отображения информации владельцу вещи.
     *
     * @param itemId идентификатор вещи
     * @param now текущая дата и время
     * @return {@link Optional} с последним завершенным бронированием или пустой, если его нет
     */
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < :now " +
            "ORDER BY b.end DESC")
    Optional<Booking> findLastBookingByItemId(@Param("itemId") Long itemId,
                                              @Param("now") LocalDateTime now);

    /**
     * Находит следующее будущее бронирование для указанной вещи.
     * Используется для отображения информации владельцу вещи.
     *
     * @param itemId идентификатор вещи
     * @param now текущая дата и время
     * @return {@link Optional} со следующим будущим бронированием или пустой, если его нет
     */
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start > :now " +
            "ORDER BY b.start ASC")
    Optional<Booking> findNextBookingByItemId(@Param("itemId") Long itemId,
                                              @Param("now") LocalDateTime now);

}
