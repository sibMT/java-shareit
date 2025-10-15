package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseJsonTest {

    @Autowired
    JacksonTester<BookingResponse> json;

    @Test
    void serialize_deserialize() throws Exception {
        var dto = new BookingResponse(
                5L,
                LocalDateTime.of(2025, 1, 1, 12, 0),
                LocalDateTime.of(2025, 1, 1, 14, 0),
                BookingStatus.APPROVED,
                new ItemShortDto(2L, "Gitara"),
                new UserShortDto(3L)
        );

        var content = json.write(dto);
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(5);
        assertThat(content).extractingJsonPathStringValue("$.item.name").isEqualTo("Gitara");
        assertThat(content).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");

        var parsed = json.parseObject(content.getJson());
        assertThat(parsed.getItem().getId()).isEqualTo(2L);
        assertThat(parsed.getBooker().getId()).isEqualTo(3L);
    }
}

