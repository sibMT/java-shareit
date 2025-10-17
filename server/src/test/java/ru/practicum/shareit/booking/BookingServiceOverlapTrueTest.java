package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceOverlapTrueTest {

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
    void create_overlapTrue_throws() {
        var owner = new User(1L, "O", "o@ex.com");
        var booker = new User(2L, "B", "b@ex.com");
        var item = new Item(10L, "Drill", "d", true, owner, null);

        when(userRepository.findById(2L)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        when(bookingRepository.existsByItem_IdAndStatusInAndEndAfterAndStartBefore(
                eq(10L), any(List.class), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        var dto = BookingDto.builder()
                .itemId(10L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> service.createBooking(2L, dto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

