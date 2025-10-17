package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceIT {

    @Autowired
    ItemService service;
    @Autowired
    BookingService bookingService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserRepository users;

    Long ownerId, bookerId, itemId;

    @BeforeEach
    void setUp() {
        ownerId = users.save(new User(null, "Owner", "o@ex.com")).getId();
        bookerId = users.save(new User(null, "Booker", "b@ex.com")).getId();
        itemId = service.createItem(
                ownerId,
                ItemDto.builder().name("Drill").description("Cordless").available(true).build()
        ).getId();
    }

    @Test
    void ownerView_contains_last_next_and_comments() {
        var pastStart = LocalDateTime.now().minusDays(3);
        var pastEnd = LocalDateTime.now().minusDays(1);
        var past = bookingService.createBooking(
                bookerId, BookingDto.builder().itemId(itemId).start(pastStart).end(pastEnd).build()
        );
        bookingService.approveBooking(ownerId, past.getId(), true);

        var futStart = LocalDateTime.now().plusDays(2);
        var futEnd = futStart.plusDays(1);
        var future = bookingService.createBooking(
                bookerId, BookingDto.builder().itemId(itemId).start(futStart).end(futEnd).build()
        );
        bookingService.approveBooking(ownerId, future.getId(), true);

        var comment = commentService.addComment(
                bookerId, itemId, CommentCreateDto.builder().text("nice").build()
        );
        assertThat(comment.getText()).isEqualTo("nice");

        var view = service.getItemById(ownerId, itemId);
        assertThat(view.getLastBooking()).isNotNull();
        assertThat(view.getLastBooking().getId()).isEqualTo(past.getId());
        assertThat(view.getNextBooking()).isNotNull();
        assertThat(view.getNextBooking().getId()).isEqualTo(future.getId());

        assertThat(view.getComments())
                .extracting("authorName")
                .contains("Booker");
    }

    @Test
    void partial_update_does_not_overwrite_with_nulls() {
        var created = service.createItem(
                ownerId, ItemDto.builder().name("Ladder").description("3m").available(true).build()
        );

        var patched = service.updateItem(
                ownerId, created.getId(), ItemDto.builder().description(null).available(null).build()
        );

        assertThat(patched.getName()).isEqualTo("Ladder");
        assertThat(patched.getDescription()).isEqualTo("3m");
        assertThat(patched.getAvailable()).isTrue();
    }

    @Test
    void getById_notFound_and_update_notFound() {
        assertThatThrownBy(() -> service.getItemById(ownerId, 9999L))
                .isInstanceOf(NoSuchElementException.class);

        assertThatThrownBy(() -> service.updateItem(ownerId, 9999L, ItemDto.builder().name("X").build()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void search_is_case_insensitive_and_returns_only_available() {
        service.createItem(ownerId,
                ItemDto.builder().name("dRiLl PRO").description("heavy").available(true).build());
        service.createItem(ownerId,
                ItemDto.builder().name("drill mini").description("light").available(false).build());

        var found = service.searchItem("DRILL");

        assertThat(found)
                .extracting(ItemDto::getName)
                .contains("dRiLl PRO")
                .doesNotContain("drill mini");
    }

    @Test
    void nonOwnerView_hasNoLastNext_butSeesComments() {
        var pastStart = LocalDateTime.now().minusDays(3);
        var pastEnd = LocalDateTime.now().minusDays(1);
        var past = bookingService.createBooking(
                bookerId, BookingDto.builder().itemId(itemId).start(pastStart).end(pastEnd).build());
        bookingService.approveBooking(ownerId, past.getId(), true);

        var futStart = LocalDateTime.now().plusDays(2);
        var futEnd = futStart.plusDays(1);
        var future = bookingService.createBooking(
                bookerId, BookingDto.builder().itemId(itemId).start(futStart).end(futEnd).build());
        bookingService.approveBooking(ownerId, future.getId(), true);

        commentService.addComment(bookerId, itemId,
                CommentCreateDto.builder().text("nice").build());

        var view = service.getItemById(bookerId, itemId);
        assertThat(view.getLastBooking()).isNull();
        assertThat(view.getNextBooking()).isNull();
        assertThat(view.getComments()).extracting("authorName").contains("Booker");
    }

    @Test
    void update_notOwner_forbidden() {
        Long stranger = users.save(new User(null, "X", "x@ex.com")).getId();
        assertThatThrownBy(() ->
                service.updateItem(stranger, itemId, ItemDto.builder().name("Hacked").build())
        ).isInstanceOf(SecurityException.class);
    }

    @Test
    void create_withMissingRequest_throws() {
        assertThatThrownBy(() ->
                service.createItem(ownerId,
                        ItemDto.builder().name("Saw").description("d").available(true).requestId(999L).build())
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getById_requesterNotFound_throws() {
        Long unknownUserId = 999_999L;
        assertThatThrownBy(() -> service.getItemById(unknownUserId, itemId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void addComment_withoutPastBooking_forbidden() {
        var start = LocalDateTime.now().plusDays(1);
        var end = start.plusDays(1);
        var br = bookingService.createBooking(
                bookerId, BookingDto.builder().itemId(itemId).start(start).end(end).build());
        bookingService.approveBooking(ownerId, br.getId(), true);

        assertThatThrownBy(() ->
                commentService.addComment(bookerId, itemId,
                        CommentCreateDto.builder().text("should fail").build())
        ).isInstanceOf(IllegalStateException.class);
    }
}

