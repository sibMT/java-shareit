package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemOwnerViewDtoJsonTest {

    @Autowired
    JacksonTester<ItemOwnerViewDto> json;

    @Test
    void serialize() throws Exception {
        var dto = ItemOwnerViewDto.builder()
                .id(1L).name("Drill").description("d").available(true).requestId(null)
                .lastBooking(new BookingShortDto(10L, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(12), 7L))
                .nextBooking(new BookingShortDto(11L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 8L))
                .comments(List.of(CommentDto.builder().id(3L).text("ok").authorName("A").created(LocalDateTime.now()).build()))
                .build();

        var content = json.write(dto);
        assertThat(content).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(10);
        assertThat(content).extractingJsonPathArrayValue("$.comments").hasSize(1);
    }
}
