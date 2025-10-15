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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceCancelIdempotentTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    BookingMapper bookingMapper;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    BookingServiceImpl service;

    @Test
    void cancel_whenAlreadyCanceled_returnsSameWithoutSave() {
        var owner = new User();
        owner.setId(1L);
        var booker = new User();
        booker.setId(2L);
        var item = new Item();
        item.setId(10L);
        item.setOwner(owner);
        item.setAvailable(true);

        var b = new Booking();
        b.setId(100L);
        b.setItem(item);
        b.setBooker(booker);
        b.setStart(LocalDateTime.now().plusDays(1));
        b.setEnd(b.getStart().plusDays(1));
        b.setStatus(BookingStatus.CANCELED);

        when(bookingRepository.findBookingById(100L)).thenReturn(Optional.of(b));
        when(bookingMapper.toResponse(b))
                .thenReturn(BookingResponse.builder().id(100L).status(BookingStatus.CANCELED).build());

        var result = service.cancelBooking(2L, 100L);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CANCELED);
        verify(bookingRepository, never()).save(any());
        verify(bookingRepository).findBookingById(100L);
        verify(bookingMapper).toResponse(b);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }
}




