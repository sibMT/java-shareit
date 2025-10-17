package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceUserNotFoundTest {

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
    void addComment_userNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.addComment(99L, 10L, CommentCreateDto.builder().text("ok").build())
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found: 99");
    }

}
