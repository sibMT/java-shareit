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
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingServiceIT {

    @Autowired
    BookingService service;
    @Autowired
    ItemService itemService;
    @Autowired
    UserRepository users;

    Long ownerId, bookerId, otherId, itemId;

    @BeforeEach
    void setup() {
        ownerId = users.save(new User(null, "Owner", "o@ex.com")).getId();
        bookerId = users.save(new User(null, "Booker", "b@ex.com")).getId();
        otherId = users.save(new User(null, "Other", "x@ex.com")).getId();

        itemId = itemService.createItem(
                ownerId,
                ItemDto.builder().name("Drill").description("d").available(true).build()
        ).getId();
    }

    @Test
    void create_and_approve_then_getByBookerAndOwner_ALL() {
        var start = LocalDateTime.now().plusDays(1);
        var end = start.plusDays(1);

        var created = service.createBooking(bookerId,
                BookingDto.builder().itemId(itemId).start(start).end(end).build());

        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);

        var approved = service.approveBooking(ownerId, created.getId(), true);
        assertThat(approved.getStatus()).isEqualTo(BookingStatus.APPROVED);

        List<BookingResponse> byBookerAll = service.getBookingsByBooker(bookerId, "ALL");
        assertThat(byBookerAll).extracting(BookingResponse::getId).contains(created.getId());

        List<BookingResponse> byOwnerAll = service.getBookingsByOwner(ownerId, "ALL");
        assertThat(byOwnerAll).extracting(BookingResponse::getId).contains(created.getId());
    }

    @Test
    void approve_twice_fails() {
        var start = LocalDateTime.now().plusDays(1);
        var end = start.plusDays(2);

        var br = service.createBooking(bookerId,
                BookingDto.builder().itemId(itemId).start(start).end(end).build());

        service.approveBooking(ownerId, br.getId(), true);
        assertThatThrownBy(() -> service.approveBooking(ownerId, br.getId(), true))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void reject_path_and_lists_WAITING_REJECTED() {
        var s1 = LocalDateTime.now().plusDays(2);
        var e1 = s1.plusDays(1);
        var waiting = service.createBooking(bookerId,
                BookingDto.builder().itemId(itemId).start(s1).end(e1).build());

        var s2 = LocalDateTime.now().plusDays(4);
        var e2 = s2.plusDays(1);
        var forReject = service.createBooking(bookerId,
                BookingDto.builder().itemId(itemId).start(s2).end(e2).build());
        var rejected = service.approveBooking(ownerId, forReject.getId(), false);

        assertThat(waiting.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(rejected.getStatus()).isEqualTo(BookingStatus.REJECTED);

        assertThat(service.getBookingsByBooker(bookerId, "WAITING"))
                .extracting(BookingResponse::getId).contains(waiting.getId());

        assertThat(service.getBookingsByBooker(bookerId, "REJECTED"))
                .extracting(BookingResponse::getId).contains(rejected.getId());

        assertThat(service.getBookingsByOwner(ownerId, "REJECTED"))
                .extracting(BookingResponse::getId).contains(rejected.getId());
    }

    @Test
    void getBookingById_access_control_owner_and_booker_ok_other_forbidden() {
        var start = LocalDateTime.now().plusDays(1);
        var end = start.plusDays(1);

        var br = service.createBooking(bookerId,
                BookingDto.builder().itemId(itemId).start(start).end(end).build());

        var asOwner = service.getBookingById(ownerId, br.getId());
        assertThat(asOwner.getId()).isEqualTo(br.getId());

        var asBooker = service.getBookingById(bookerId, br.getId());
        assertThat(asBooker.getId()).isEqualTo(br.getId());

        assertThatThrownBy(() -> service.getBookingById(otherId, br.getId()))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void states_CURRENT_PAST_FUTURE_for_booker_and_owner() {
        var pastStart = LocalDateTime.now().minusDays(3);
        var pastEnd = LocalDateTime.now().minusDays(1);
        var past = service.createBooking(bookerId,
                BookingDto.builder().itemId(itemId).start(pastStart).end(pastEnd).build());
        service.approveBooking(ownerId, past.getId(), true);

        var curStart = LocalDateTime.now().minusHours(1);
        var curEnd = LocalDateTime.now().plusHours(2);
        var current = service.createBooking(bookerId,
                BookingDto.builder().itemId(itemId).start(curStart).end(curEnd).build());
        service.approveBooking(ownerId, current.getId(), true);

        var futStart = LocalDateTime.now().plusDays(2);
        var futEnd = futStart.plusDays(1);
        var future = service.createBooking(bookerId,
                BookingDto.builder().itemId(itemId).start(futStart).end(futEnd).build());
        service.approveBooking(ownerId, future.getId(), true);

        assertThat(service.getBookingsByBooker(bookerId, "PAST"))
                .extracting(BookingResponse::getId).contains(past.getId());
        assertThat(service.getBookingsByBooker(bookerId, "CURRENT"))
                .extracting(BookingResponse::getId).contains(current.getId());
        assertThat(service.getBookingsByBooker(bookerId, "FUTURE"))
                .extracting(BookingResponse::getId).contains(future.getId());

        assertThat(service.getBookingsByOwner(ownerId, "PAST"))
                .extracting(BookingResponse::getId).contains(past.getId());
        assertThat(service.getBookingsByOwner(ownerId, "CURRENT"))
                .extracting(BookingResponse::getId).contains(current.getId());
        assertThat(service.getBookingsByOwner(ownerId, "FUTURE"))
                .extracting(BookingResponse::getId).contains(future.getId());
    }

    @Test
    void owner_cannot_book_his_item() {
        var start = LocalDateTime.now().plusHours(1);
        var end = start.plusHours(2);

        assertThatThrownBy(() -> service.createBooking(ownerId,
                BookingDto.builder().itemId(itemId).start(start).end(end).build()))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void invalid_interval_rejected() {
        var now = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> service.createBooking(bookerId,
                BookingDto.builder().itemId(itemId).start(now).end(now).build()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getBookingById_notFound() {
        assertThatThrownBy(() -> service.getBookingById(bookerId, 9999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void cancelBooking_success_and_alreadyStarted_orNotAuthor_fails() {
        var start = LocalDateTime.now().plusHours(2);
        var end = start.plusHours(2);

        var booking = service.createBooking(bookerId,
                BookingDto.builder().itemId(itemId).start(start).end(end).build());
        service.approveBooking(ownerId, booking.getId(), true);

        var canceled = service.cancelBooking(bookerId, booking.getId());
        assertThat(canceled.getStatus()).isEqualTo(BookingStatus.CANCELED);

        var canceledAgain = service.cancelBooking(bookerId, booking.getId());
        assertThat(canceledAgain.getStatus()).isEqualTo(BookingStatus.CANCELED);

        assertThatThrownBy(() -> service.cancelBooking(otherId, booking.getId()))
                .isInstanceOf(SecurityException.class);

        var started = service.createBooking(bookerId,
                BookingDto.builder()
                        .itemId(itemId)
                        .start(LocalDateTime.now().minusHours(2))
                        .end(LocalDateTime.now().plusHours(1))
                        .build());
        service.approveBooking(ownerId, started.getId(), true);

        assertThatThrownBy(() -> service.cancelBooking(bookerId, started.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void approveLatestWaiting_noWaitingBookings_fails() {
        assertThatThrownBy(() -> service.approveLatestWaiting(ownerId, true))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    void createBooking_itemUnavailable_rejected() {
        itemService.updateItem(ownerId,
                itemId,
                ItemDto.builder().available(false).build());

        var start = LocalDateTime.now().plusDays(1);
        var end = start.plusDays(1);

        assertThatThrownBy(() -> service.createBooking(bookerId,
                BookingDto.builder().itemId(itemId).start(start).end(end).build()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}



