package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceStateWaitingBookerTest {
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

    @Test
    void getByBooker_waiting_routesToWaitingQuery() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "U", "u@ex.com")));

        var booking = new Booking();
        booking.setId(10L);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findByBookerAndStatus(1L, BookingStatus.WAITING))
                .thenReturn(List.of(booking));
        when(bookingMapper.toResponse(booking))
                .thenReturn(BookingResponse.builder().id(10L).status(BookingStatus.WAITING).build());

        var out = service.getBookingsByBooker(1L, "WAITING");

        assertThat(out.size()).isEqualTo(1);
        assertThat(out.get(0).getId()).isEqualTo(10L);
        verify(bookingRepository).findByBookerAndStatus(1L, BookingStatus.WAITING);
    }

    @Test
    void getByOwner_waiting_routesToWaitingQuery() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(new User(7L, "O", "o@ex.com")));

        var booking = new Booking();
        booking.setId(20L);
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findByOwnerAndStatus(7L, BookingStatus.WAITING))
                .thenReturn(List.of(booking));
        when(bookingMapper.toResponse(booking))
                .thenReturn(BookingResponse.builder().id(20L).status(BookingStatus.WAITING).build());

        var out = service.getBookingsByOwner(7L, "WAITING");

        assertThat(out.size()).isEqualTo(1);
        assertThat(out.get(0).getId()).isEqualTo(20L);
        verify(bookingRepository).findByOwnerAndStatus(7L, BookingStatus.WAITING);
    }

    @Test
    void getByOwner_rejected_routesToRejectedQuery() {
        when(userRepository.findById(7L)).thenReturn(Optional.of(new User(7L, "O", "o@ex.com")));

        var booking = new Booking();
        booking.setId(21L);
        booking.setStatus(BookingStatus.REJECTED);

        when(bookingRepository.findByOwnerAndStatus(7L, BookingStatus.REJECTED))
                .thenReturn(List.of(booking));
        when(bookingMapper.toResponse(booking))
                .thenReturn(BookingResponse.builder().id(21L).status(BookingStatus.REJECTED).build());

        var out = service.getBookingsByOwner(7L, "REJECTED");

        assertThat(out.size()).isEqualTo(1);
        assertThat(out.get(0).getId()).isEqualTo(21L);
        verify(bookingRepository).findByOwnerAndStatus(7L, BookingStatus.REJECTED);
    }

    @Test
    void getByBooker_rejected_routesToRejectedQuery() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "U", "u@ex.com")));

        var booking = new Booking();
        booking.setId(11L);
        booking.setStatus(BookingStatus.REJECTED);

        when(bookingRepository.findByBookerAndStatus(1L, BookingStatus.REJECTED))
                .thenReturn(List.of(booking));
        when(bookingMapper.toResponse(booking))
                .thenReturn(BookingResponse.builder().id(11L).status(BookingStatus.REJECTED).build());

        var out = service.getBookingsByBooker(1L, "REJECTED");

        assertThat(out.size()).isEqualTo(1);
        assertThat(out.get(0).getId()).isEqualTo(11L);
        verify(bookingRepository).findByBookerAndStatus(1L, BookingStatus.REJECTED);
    }

    @Test
    void getByBooker_stateNull_routesToAll() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "U", "u@ex.com")));

        var b = new Booking();
        b.setId(30L);

        when(bookingRepository.findAllByBookerOrderByStartDesc(1L))
                .thenReturn(List.of(b));
        when(bookingMapper.toResponse(b))
                .thenReturn(BookingResponse.builder().id(30L).build());

        var out = service.getBookingsByBooker(1L, null);

        assertThat(out.size()).isEqualTo(1);
        assertThat(out.get(0).getId()).isEqualTo(30L);
    }
}

