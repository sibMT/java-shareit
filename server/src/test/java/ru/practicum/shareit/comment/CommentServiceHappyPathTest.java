package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceHappyPathTest {

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
    void addComment_success() {
        var user = new User(2L, "B", "b@ex.com");
        var owner = new User(1L, "O", "o@ex.com");
        var item = new Item(10L, "Drill", "d", true, owner, null);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                eq(2L), eq(10L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)
        )).thenReturn(true);

        when(commentMapper.toEntity(any(CommentCreateDto.class), any(), any()))
                .thenAnswer(inv -> {
                    CommentCreateDto dto = inv.getArgument(0);

                    Object a1 = inv.getArgument(1);
                    Object a2 = inv.getArgument(2);

                    Item it = (a1 instanceof Item) ? (Item) a1 : (Item) a2;
                    User us = (a1 instanceof User) ? (User) a1 : (User) a2;

                    Comment entity = new Comment();
                    entity.setText(dto.getText());
                    entity.setItem(it);
                    entity.setAuthor(us);
                    entity.setCreated(LocalDateTime.now());
                    return entity;
                });

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(inv -> {
                    Comment c = inv.getArgument(0);
                    try {
                        c.setId(5L);
                    } catch (Throwable ignore) {
                    }
                    return c;
                });

        when(commentMapper.toDto(any(Comment.class)))
                .thenAnswer(inv -> {
                    Comment c = inv.getArgument(0);
                    return CommentDto.builder()
                            .id(c.getId() != null ? c.getId() : 5L)
                            .text(c.getText())
                            .authorName(c.getAuthor() != null ? c.getAuthor().getName() : null)
                            .created(c.getCreated())
                            .build();
                });

        var dto = service.addComment(2L, 10L, CommentCreateDto.builder().text("nice").build());

        org.assertj.core.api.Assertions.assertThat(dto.getId()).isEqualTo(5L);
        org.assertj.core.api.Assertions.assertThat(dto.getAuthorName()).isEqualTo("B");
        org.assertj.core.api.Assertions.assertThat(dto.getText()).isEqualTo("nice");
    }
}

