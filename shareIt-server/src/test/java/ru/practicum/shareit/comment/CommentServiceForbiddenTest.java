package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.mockito.ArgumentMatchers.*;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceForbiddenTest {
    @Mock
    CommentRepository commentRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentMapper commentMapper;
    @InjectMocks
    CommentServiceImpl service;

    @Test
    void addComment_forbiddenWithoutPastBooking() {
        var user = new User(2L, "B", "b@ex.com");
        var owner = new User(1L, "O", "o@ex.com");
        var item = new Item(10L, "Drill", "d", true, owner, null);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                eq(2L),
                eq(10L),
                eq(BookingStatus.APPROVED),
                any(java.time.LocalDateTime.class)
        )).thenReturn(false);

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        service.addComment(2L, 10L, CommentCreateDto.builder().text("hi").build()))
                .isInstanceOf(IllegalStateException.class);
    }
}
