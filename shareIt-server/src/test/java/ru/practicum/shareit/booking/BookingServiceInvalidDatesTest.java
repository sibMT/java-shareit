package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceInvalidDatesTest {
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
    void create_fails_whenStartOrEndNull_orEndNotAfterStart() {
        var owner = new User();
        owner.setId(1L);
        var booker = new User();
        booker.setId(2L);
        var item = new Item();
        item.setId(10L);
        item.setOwner(owner);
        item.setAvailable(true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        var t = java.time.LocalDateTime.now().plusDays(1);
        var same = BookingDto.builder().itemId(10L).start(t).end(t).build();
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.createBooking(2L, same))
                .isInstanceOf(IllegalArgumentException.class);

        var bad1 = BookingDto.builder().itemId(10L).start(null).end(t.plusDays(1)).build();
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.createBooking(2L, bad1))
                .isInstanceOf(IllegalArgumentException.class);

        var bad2 = BookingDto.builder().itemId(10L).start(t).end(null).build();
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.createBooking(2L, bad2))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
