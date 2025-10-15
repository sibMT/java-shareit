package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
class BookingServiceApproveNotOwnerTest {

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
    void approve_notOwner_forbidden() {
        var owner = new User(1L, "Owner", "o@ex.com");
        var notOwner = new User(2L, "X", "x@ex.com");
        var booker = new User(3L, "B", "b@ex.com");
        var item = new Item(10L, "Drill", "d", true, owner, null);

        var b = new Booking();
        b.setId(100L);
        b.setItem(item);
        b.setBooker(booker);
        b.setStart(LocalDateTime.now().plusDays(1));
        b.setEnd(LocalDateTime.now().plusDays(2));
        b.setStatus(BookingStatus.WAITING);

        when(bookingRepository.findBookingById(100L)).thenReturn(Optional.of(b));

        assertThatThrownBy(() -> service.approveBooking(notOwner.getId(), 100L, true))
                .isInstanceOf(SecurityException.class);
    }
}


