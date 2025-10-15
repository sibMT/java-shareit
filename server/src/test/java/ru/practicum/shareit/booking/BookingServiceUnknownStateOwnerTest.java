package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnknownStateOwnerTest {

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
    void getByOwner_unknownState_throwsIAE() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "O", "o@ex.com")));

        assertThatThrownBy(() -> service.getBookingsByOwner(1L, "SOMETHING"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown state");

        verifyNoInteractions(bookingRepository);
    }
}

