package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CommentMapperTest {

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void toDto() {
        var author = new User(2L, "A", "a@ex.com");
        var item = new Item(3L, "D", "d", true, new User(1L, "O", "o@ex.com"), null);
        var created = LocalDateTime.of(2025, 1, 1, 10, 0);
        var entity = new Comment(7L, "Nice", item, author, created);

        var dto = mapper.toDto(entity);
        assertThat(dto.getId()).isEqualTo(7L);
        assertThat(dto.getAuthorName()).isEqualTo("A");
        assertThat(dto.getText()).isEqualTo("Nice");
    }
}
