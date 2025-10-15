package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

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

        itemId = itemService.createItem(
                ownerId,
                ItemDto.builder()
                        .name("Drill")
                        .description("d")
                        .available(true)
                        .build()
        ).getId();
    }

    @Test
    void create_and_approve() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(1);

        BookingDto req = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();

        BookingResponse created = service.createBooking(bookerId, req);
        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);

        BookingResponse approved = service.approveBooking(ownerId, created.getId(), true);
        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);

        List<BookingResponse> forBooker = service.getBookingsByBooker(bookerId, "ALL");
        assertThat(forBooker)
                .extracting(BookingResponse::getId)
                .contains(created.getId());
    }

    @Test
    void owner_cannot_book_his_item() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(start.plusHours(1))
                .build();

        assertThatThrownBy(() -> service.createBooking(ownerId, dto))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void invalid_interval_rejected() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(5);
        BookingDto dto = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(start)
                .build();

        assertThatThrownBy(() -> service.createBooking(bookerId, dto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}


