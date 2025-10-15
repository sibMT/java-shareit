package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    JacksonTester<CommentDto> json;

    @Autowired
    JacksonTester<CommentCreateDto> jsonCreate;

    @Test
    void serialize() throws Exception {
        var dto = CommentDto.builder()
                .id(1L)
                .text("Nice")
                .authorName("Max")
                .created(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();

        var content = json.write(dto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("Nice");
        assertThat(content).extractingJsonPathStringValue("$.authorName").isEqualTo("Max");
        assertThat(content).extractingJsonPathStringValue("$.created").isEqualTo("2025-01-01T10:00:00");
    }

    @Test
    void write_read() throws Exception {
        var create = CommentCreateDto.builder()
                .text("good")
                .build();
        var content = jsonCreate.write(create);
        assertThat(content).extractingJsonPathStringValue("$.text").isEqualTo("good");

        var parsed = jsonCreate.parseObject("{\"text\":\"good\"}");
        assertThat(parsed.getText()).isEqualTo("good");
    }
}

