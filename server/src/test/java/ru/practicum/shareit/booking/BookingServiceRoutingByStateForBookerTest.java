package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceRoutingByStateForBookerTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingMapper bookingMapper;

    @InjectMocks
    BookingServiceImpl service;

    private static BookingResponse dto(long id, BookingStatus st) {
        return BookingResponse.builder().id(id).status(st).build();
    }

    private static Booking entity(long id, BookingStatus st) {
        var b = new Booking();
        b.setId(id);
        b.setStatus(st);
        b.setStart(LocalDateTime.now().minusDays(1));
        b.setEnd(LocalDateTime.now().plusDays(1));
        return b;
    }

    @Test
    void routes_all_states_for_booker() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "U", "u@e.com")));

        var all = List.of(entity(1, BookingStatus.APPROVED));
        when(bookingRepository.findAllByBookerOrderByStartDesc(1L)).thenReturn(all);
        when(bookingMapper.toResponse(all.get(0))).thenReturn(dto(1, BookingStatus.APPROVED));
        assertThat(service.getBookingsByBooker(1L, "ALL"))
                .extracting(BookingResponse::getId).containsExactly(1L);
        verify(bookingRepository).findAllByBookerOrderByStartDesc(1L);

        reset(bookingRepository, bookingMapper);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "U", "u@e.com")));
        var cur = List.of(entity(2, BookingStatus.APPROVED));
        when(bookingRepository.findCurrentByBooker(eq(1L), any(LocalDateTime.class))).thenReturn(cur);
        when(bookingMapper.toResponse(cur.get(0))).thenReturn(dto(2, BookingStatus.APPROVED));
        assertThat(service.getBookingsByBooker(1L, "CURRENT"))
                .extracting(BookingResponse::getId).containsExactly(2L);
        verify(bookingRepository).findCurrentByBooker(eq(1L), any(LocalDateTime.class));

        reset(bookingRepository, bookingMapper);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "U", "u@e.com")));
        var past = List.of(entity(3, BookingStatus.APPROVED));
        when(bookingRepository.findPastByBooker(eq(1L), any(LocalDateTime.class))).thenReturn(past);
        when(bookingMapper.toResponse(past.get(0))).thenReturn(dto(3, BookingStatus.APPROVED));
        assertThat(service.getBookingsByBooker(1L, "PAST"))
                .extracting(BookingResponse::getId).containsExactly(3L);
        verify(bookingRepository).findPastByBooker(eq(1L), any(LocalDateTime.class));

        reset(bookingRepository, bookingMapper);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "U", "u@e.com")));
        var fut = List.of(entity(4, BookingStatus.WAITING));
        when(bookingRepository.findFutureByBooker(eq(1L), any(LocalDateTime.class))).thenReturn(fut);
        when(bookingMapper.toResponse(fut.get(0))).thenReturn(dto(4, BookingStatus.WAITING));
        assertThat(service.getBookingsByBooker(1L, "FUTURE"))
                .extracting(BookingResponse::getId).containsExactly(4L);
        verify(bookingRepository).findFutureByBooker(eq(1L), any(LocalDateTime.class));

        reset(bookingRepository, bookingMapper);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "U", "u@e.com")));
        var w = List.of(entity(5, BookingStatus.WAITING));
        when(bookingRepository.findByBookerAndStatus(1L, BookingStatus.WAITING)).thenReturn(w);
        when(bookingMapper.toResponse(w.get(0))).thenReturn(dto(5, BookingStatus.WAITING));
        assertThat(service.getBookingsByBooker(1L, "WAITING"))
                .extracting(BookingResponse::getStatus).containsExactly(BookingStatus.WAITING);
        verify(bookingRepository).findByBookerAndStatus(1L, BookingStatus.WAITING);

        reset(bookingRepository, bookingMapper);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "U", "u@e.com")));
        var r = List.of(entity(6, BookingStatus.REJECTED));
        when(bookingRepository.findByBookerAndStatus(1L, BookingStatus.REJECTED)).thenReturn(r);
        when(bookingMapper.toResponse(r.get(0))).thenReturn(dto(6, BookingStatus.REJECTED));
        assertThat(service.getBookingsByBooker(1L, "REJECTED"))
                .extracting(BookingResponse::getStatus).containsExactly(BookingStatus.REJECTED);
        verify(bookingRepository).findByBookerAndStatus(1L, BookingStatus.REJECTED);
    }
}


