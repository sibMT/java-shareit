package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceItemNotFoundTest {

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
    void addComment_itemNotFound_throws() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "U", "u@ex.com")));
        when(itemRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.addComment(2L, 404L, CommentCreateDto.builder().text("ok").build())
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Item not found: 404");
    }
}

