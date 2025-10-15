package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceViewerIT {

    @Autowired
    ItemService itemService;
    @Autowired
    BookingService bookingService;
    @Autowired
    UserRepository users;

    Long ownerId, viewerId, itemId;

    @BeforeEach
    void setUp() {
        ownerId = users.save(new User(null, "Owner", "o@ex.com")).getId();
        viewerId = users.save(new User(null, "Viewer", "v@ex.com")).getId();
        itemId = itemService.createItem(ownerId,
                ItemDto.builder().name("Drill").description("d").available(true).build()).getId();

        var pastS = LocalDateTime.now().minusDays(3);
        var pastE = LocalDateTime.now().minusDays(1);
        var b1 = bookingService.createBooking(viewerId,
                BookingDto.builder().itemId(itemId).start(pastS).end(pastE).build());
        bookingService.approveBooking(ownerId, b1.getId(), true);

        var futS = LocalDateTime.now().plusDays(2);
        var futE = futS.plusDays(1);
        var b2 = bookingService.createBooking(viewerId,
                BookingDto.builder().itemId(itemId).start(futS).end(futE).build());
        bookingService.approveBooking(ownerId, b2.getId(), true);
    }

    @Test
    void get_by_not_owner_has_no_last_next() {
        var view = itemService.getItemById(viewerId, itemId);
        assertThat(view.getLastBooking()).isNull();
        assertThat(view.getNextBooking()).isNull();
    }
}
