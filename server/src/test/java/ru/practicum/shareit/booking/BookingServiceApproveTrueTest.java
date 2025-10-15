package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceApproveTrueTest {

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
    void approve_waiting_true_setsApproved() {
        var owner = new User(1L, "Owner", "o@ex.com");
        var booker = new User(2L, "B", "b@ex.com");
        var item = new Item(10L, "D", "d", true, owner, null);

        var b = new Booking();
        b.setId(100L);
        b.setItem(item);
        b.setBooker(booker);
        b.setStart(LocalDateTime.now().plusDays(1));
        b.setEnd(LocalDateTime.now().plusDays(2));
        b.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findBookingById(100L)).thenReturn(Optional.of(b));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        when(bookingMapper.toResponse(any(Booking.class))).thenAnswer(inv -> {
            var saved = (Booking) inv.getArgument(0);
            return BookingResponse.builder()
                    .id(saved.getId())
                    .status(saved.getStatus())
                    .build();
        });

        var out = service.approveBooking(1L, 100L, true);

        assertThat(out.getStatus()).isEqualTo(BookingStatus.APPROVED);

        verify(bookingRepository).save(argThat(saved -> saved.getId().equals(100L)
                && saved.getStatus() == BookingStatus.APPROVED));
    }
}

