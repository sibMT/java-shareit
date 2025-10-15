package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceAccessDeniedGetByIdTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    BookingMapper bookingMapper;
    @Mock
    ru.practicum.shareit.user.UserRepository userRepository;
    @Mock
    ru.practicum.shareit.item.ItemRepository itemRepository;

    @InjectMocks
    BookingServiceImpl service;

    @Test
    void getById_forbidden_for_third_user() {
        var owner = new User(1L, "Owner", "o@ex.com");
        var booker = new User(2L, "B", "b@ex.com");
        var strangerId = 3L;

        var item = new Item(10L, "D", "d", true, owner, null);

        var booking = new Booking();
        booking.setId(5L);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findBookingById(5L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.getBookingById(strangerId, 5L))
                .isInstanceOf(SecurityException.class);
    }
}


