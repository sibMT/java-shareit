package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceGetByIdNotFoundTest {

    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemMapper itemMapper;

    @InjectMocks
    ItemServiceImpl service;

    @Test
    void getById_notFound_throws() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "U", "u@ex.com")));

        when(itemRepository.findById(777L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getItemById(1L, 777L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
