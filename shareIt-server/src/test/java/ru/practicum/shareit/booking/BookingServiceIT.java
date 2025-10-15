package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceIT {

    @Autowired
    BookingService service;
    @Autowired
    ItemService itemService;
    @Autowired
    UserRepository users;

    Long ownerId, bookerId, itemId;

    @BeforeEach
    void setup() {
        ownerId = users.save(new User(null, "Owner", "o@ex.com")).getId();
        bookerId = users.save(new User(null, "Book", "b@ex.com")).getId();
        itemId = itemService.createItem(ownerId,
                ItemDto.builder().name("Drill").description("d").available(true).build()).getId();
    }

    @Test
    void create_and_approve_ok() {
        var now = LocalDateTime.now().plusMinutes(5);
        var dto = BookingDto.builder()
                .itemId(itemId)
                .start(now)
                .end(now.plusHours(2))
                .build();

        BookingResponse created = service.createBooking(bookerId, dto);
        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);

        var approved = service.approveBooking(ownerId, created.getId(), true);
        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void owner_cannot_book_his_item() {
        var now = LocalDateTime.now().plusMinutes(5);
        var dto = BookingDto.builder().itemId(itemId).start(now).end(now.plusHours(1)).build();
        assertThatThrownBy(() -> service.createBooking(ownerId, dto))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void invalid_interval_rejected() {
        var now = LocalDateTime.now().plusMinutes(5);
        var dto = BookingDto.builder().itemId(itemId).start(now).end(now).build();
        assertThatThrownBy(() -> service.createBooking(bookerId, dto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

