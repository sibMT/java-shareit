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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceGetByIdAllowedTest {

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
    void getById_ownerAllowed_returnsResponse() {
        var owner = new User(1L, "Owner", "o@ex.com");
        var booker = new User(2L, "B", "b@ex.com");
        var item = new Item(10L, "D", "d", true, owner, null);

        var booking = new Booking();
        booking.setId(5L);
        booking.setItem(item);
        booking.setBooker(booker);

        when(bookingRepository.findBookingById(5L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toResponse(any(Booking.class)))
                .thenReturn(BookingResponse.builder().id(5L).build());

        var out = service.getBookingById(1L, 5L);
        assertThat(out.getId()).isEqualTo(5L);
    }
}
