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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceOverlapTest {
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
    void create_fails_whenOverlaps() {
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
        when(bookingRepository.existsByItem_IdAndStatusInAndEndAfterAndStartBefore(
                eq(10L), any(), any(), any()))
                .thenReturn(true);

        var start = java.time.LocalDateTime.now().plusDays(1);
        var end = start.plusDays(1);
        var dto = BookingDto.builder().itemId(10L).start(start).end(end).build();

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.createBooking(2L, dto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
