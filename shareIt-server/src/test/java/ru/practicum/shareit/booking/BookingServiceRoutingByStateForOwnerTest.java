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
class BookingServiceRoutingByStateForOwnerTest {

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

    private static Booking entity(long id, BookingStatus st) {
        var b = new Booking();
        b.setId(id);
        b.setStatus(st);
        b.setStart(LocalDateTime.now().minusDays(1));
        b.setEnd(LocalDateTime.now().plusDays(1));
        return b;
    }

    private static BookingResponse dto(long id, BookingStatus st) {
        return BookingResponse.builder().id(id).status(st).build();
    }

    @Test
    void routes_all_states_for_owner() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "O", "o@e.com")));

        var all = List.of(entity(11, BookingStatus.APPROVED));
        when(bookingRepository.findAllByOwnerOrderByStartDesc(2L)).thenReturn(all);
        when(bookingMapper.toResponse(all.get(0))).thenReturn(dto(11, BookingStatus.APPROVED));
        assertThat(service.getBookingsByOwner(2L, "ALL"))
                .extracting(BookingResponse::getId).containsExactly(11L);
        verify(bookingRepository).findAllByOwnerOrderByStartDesc(2L);

        reset(bookingRepository, bookingMapper);
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "O", "o@e.com")));
        var cur = List.of(entity(12, BookingStatus.APPROVED));
        when(bookingRepository.findCurrentByOwner(eq(2L), any(LocalDateTime.class))).thenReturn(cur);
        when(bookingMapper.toResponse(cur.get(0))).thenReturn(dto(12, BookingStatus.APPROVED));
        assertThat(service.getBookingsByOwner(2L, "CURRENT"))
                .extracting(BookingResponse::getId).containsExactly(12L);
        verify(bookingRepository).findCurrentByOwner(eq(2L), any(LocalDateTime.class));

        reset(bookingRepository, bookingMapper);
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "O", "o@e.com")));
        var past = List.of(entity(13, BookingStatus.APPROVED));
        when(bookingRepository.findPastByOwner(eq(2L), any(LocalDateTime.class))).thenReturn(past);
        when(bookingMapper.toResponse(past.get(0))).thenReturn(dto(13, BookingStatus.APPROVED));
        assertThat(service.getBookingsByOwner(2L, "PAST"))
                .extracting(BookingResponse::getId).containsExactly(13L);
        verify(bookingRepository).findPastByOwner(eq(2L), any(LocalDateTime.class));

        reset(bookingRepository, bookingMapper);
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "O", "o@e.com")));
        var fut = List.of(entity(14, BookingStatus.WAITING));
        when(bookingRepository.findFutureByOwner(eq(2L), any(LocalDateTime.class))).thenReturn(fut);
        when(bookingMapper.toResponse(fut.get(0))).thenReturn(dto(14, BookingStatus.WAITING));
        assertThat(service.getBookingsByOwner(2L, "FUTURE"))
                .extracting(BookingResponse::getId).containsExactly(14L);
        verify(bookingRepository).findFutureByOwner(eq(2L), any(LocalDateTime.class));

        reset(bookingRepository, bookingMapper);
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "O", "o@e.com")));
        var w = List.of(entity(15, BookingStatus.WAITING));
        when(bookingRepository.findByOwnerAndStatus(2L, BookingStatus.WAITING)).thenReturn(w);
        when(bookingMapper.toResponse(w.get(0))).thenReturn(dto(15, BookingStatus.WAITING));
        assertThat(service.getBookingsByOwner(2L, "WAITING"))
                .extracting(BookingResponse::getStatus).containsExactly(BookingStatus.WAITING);
        verify(bookingRepository).findByOwnerAndStatus(2L, BookingStatus.WAITING);

        reset(bookingRepository, bookingMapper);
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "O", "o@e.com")));
        var r = List.of(entity(16, BookingStatus.REJECTED));
        when(bookingRepository.findByOwnerAndStatus(2L, BookingStatus.REJECTED)).thenReturn(r);
        when(bookingMapper.toResponse(r.get(0))).thenReturn(dto(16, BookingStatus.REJECTED));
        assertThat(service.getBookingsByOwner(2L, "REJECTED"))
                .extracting(BookingResponse::getStatus).containsExactly(BookingStatus.REJECTED);
        verify(bookingRepository).findByOwnerAndStatus(2L, BookingStatus.REJECTED);
    }
}

