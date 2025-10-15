package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    JacksonTester<CommentDto> json;

    @Test
    void serialize() throws Exception {
        var dto = CommentDto.builder()
                .id(1L).text("Nice").authorName("Max")
                .created(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();
        var content = json.write(dto);
        assertThat(content).extractingJsonPathStringValue("$.authorName").isEqualTo("Max");
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
    }
}
