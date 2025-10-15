package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    JacksonTester<BookingDto> json;

    @Test
    void write_read() throws Exception {
        var dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2030, 1, 1, 10, 0))
                .end(LocalDateTime.of(2030, 1, 2, 10, 0))
                .status(BookingStatus.WAITING)
                .build();

        var content = json.write(dto);
        assertThat(content).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");

        var parsed = json.parseObject(content.getJson());
        assertThat(parsed.getStart()).isEqualTo(dto.getStart());
        assertThat(parsed.getEnd()).isEqualTo(dto.getEnd());
    }
}
