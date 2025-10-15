package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceRejectTest {

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
    void approve_setsRejected_whenFalse() {
        var owner = new User();
        owner.setId(1L);
        var booker = new User();
        booker.setId(2L);
        var item = new Item();
        item.setId(10L);
        item.setOwner(owner);
        item.setAvailable(true);

        var booking = new Booking();
        booking.setId(100L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(java.time.LocalDateTime.now().plusDays(1));
        booking.setEnd(booking.getStart().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findBookingById(100L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingMapper.toResponse(any(Booking.class)))
                .thenAnswer(inv -> {
                    Booking b = inv.getArgument(0);
                    return BookingResponse.builder()
                            .id(b.getId())
                            .status(b.getStatus())
                            .build();
                });

        var result = service.approveBooking(owner.getId(), 100L, false);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.REJECTED);
        verify(bookingRepository).save(argThat(b -> b.getStatus() == BookingStatus.REJECTED));
    }
}

