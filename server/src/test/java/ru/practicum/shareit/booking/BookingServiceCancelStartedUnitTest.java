package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceCancelStartedUnitTest {

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
    void cancel_afterStart_fails() {
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
        b.setStart(LocalDateTime.now().minusHours(2));
        b.setEnd(LocalDateTime.now().plusHours(2));
        b.setStatus(BookingStatus.APPROVED);

        when(bookingRepository.findBookingById(100L)).thenReturn(Optional.of(b));

        assertThatThrownBy(() -> service.cancelBooking(2L, 100L))
                .isInstanceOf(IllegalStateException.class);
    }
}

