package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponse createBooking(Long bookerId, BookingDto dto) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + bookerId));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NoSuchElementException("Item не найден: id=" + dto.getItemId()));

        if (!item.isAvailable()) {
            throw new IllegalArgumentException("Item недоступен для бронирования");
        }
        if (item.getOwner().getId().equals(bookerId)) {
            throw new SecurityException("Владелец не может бронировать свою вещь");
        }
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new IllegalArgumentException("Даты не могут быть null");
        }
        if (!dto.getEnd().isAfter(dto.getStart())) {
            throw new IllegalArgumentException("Некорректный интервал времени");
        }

        boolean overlaps = bookingRepository.existsByItem_IdAndStatusInAndEndAfterAndStartBefore(
                item.getId(),
                List.of(BookingStatus.APPROVED, BookingStatus.WAITING),
                dto.getStart(),
                dto.getEnd()
        );
        if (overlaps) {
            throw new IllegalArgumentException("Есть пересечение с существующим бронированием");
        }

        Booking toSave = bookingMapper.toNewEntity(dto, item, booker);
        Booking saved = bookingRepository.save(toSave);
        return bookingMapper.toResponse(saved);
    }

    @Override
    public BookingResponse approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findBookingById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking не найден: id=" + bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new SecurityException("Подтверждать/отклонять может только владелец вещи");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new IllegalStateException("Статус уже установлен");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updated = bookingRepository.save(booking);
        return bookingMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long requesterId, Long bookingId) {
        Booking booking = bookingRepository.findBookingById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking не найден: id=" + bookingId));

        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();
        if (!ownerId.equals(requesterId) && !bookerId.equals(requesterId)) {
            throw new SecurityException("Доступ запрещён");
        }
        return bookingMapper.toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByBooker(Long bookerId, String state) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + bookerId));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state == null ? "ALL" : state.toUpperCase()) {
            case "CURRENT" -> bookingRepository.findCurrentByBooker(bookerId, now);
            case "PAST" -> bookingRepository.findPastByBooker(bookerId, now);
            case "FUTURE" -> bookingRepository.findFutureByBooker(bookerId, now);
            case "WAITING" -> bookingRepository.findByBookerAndStatus(bookerId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository.findByBookerAndStatus(bookerId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByBookerOrderByStartDesc(bookerId); // ALL
        };
        return bookings.stream().map(bookingMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByOwner(Long ownerId, String state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("User не найден: id=" + ownerId));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state == null ? "ALL" : state.toUpperCase()) {
            case "CURRENT" -> bookingRepository.findCurrentByOwner(ownerId, now);
            case "PAST" -> bookingRepository.findPastByOwner(ownerId, now);
            case "FUTURE" -> bookingRepository.findFutureByOwner(ownerId, now);
            case "WAITING" -> bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository.findByOwnerAndStatus(ownerId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByOwnerOrderByStartDesc(ownerId); // ALL
        };
        return bookings.stream().map(bookingMapper::toResponse).toList();
    }

    @Override
    public BookingResponse cancelBooking(Long bookerId, Long bookingId) {
        Booking booking = bookingRepository.findBookingById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking не найден: id=" + bookingId));

        if (!booking.getBooker().getId().equals(bookerId)) {
            throw new SecurityException("Отменить бронирование может только его автор");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Нельзя отменить уже начавшееся бронирование");
        }
        if (booking.getStatus() == BookingStatus.CANCELED) {
            return bookingMapper.toResponse(booking);
        }
        if (booking.getStatus() != BookingStatus.WAITING && booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalStateException("Текущий статус не позволяет отмену: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELED);
        Booking updated = bookingRepository.save(booking);
        return bookingMapper.toResponse(updated);
    }

    @Override
    public BookingResponse approveLatestWaiting(Long ownerId, boolean approved) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NoSuchElementException("User not found: id=" + ownerId));

        Optional<Booking> opt = bookingRepository.findLatestByOwnerAndStatusNative(
                ownerId, BookingStatus.WAITING.name());

        if (opt.isEmpty()) {
            opt = bookingRepository.findLatestByOwnerAndStatus(ownerId, BookingStatus.WAITING);
        }
        if (opt.isEmpty()) {
            opt = bookingRepository.findTopByItem_Owner_IdAndStatusOrderByIdDesc(ownerId, BookingStatus.WAITING);
        }

        Booking booking = opt.orElseThrow(() ->
                new IllegalArgumentException("bookingId is missing and there is no WAITING booking for owner=" + ownerId)
        );

        return approveBooking(ownerId, booking.getId(), approved);
    }
}
