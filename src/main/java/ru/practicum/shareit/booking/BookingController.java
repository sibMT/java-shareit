package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;


@RestController
@RequestMapping({"/bookings", "/bookings/"})
@Validated
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponse create(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @Valid @RequestBody BookingCreateRequest body
    ) {
        return bookingService.createBooking(bookerId, body);
    }

    @PatchMapping({"/{bookingId}", "/{bookingId}/"})
    public BookingResponse approveByPath(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam("approved") boolean approved
    ) {
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @PatchMapping(path = {"", "/"}, params = "bookingId")
    public BookingResponse approveByQuery(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam("bookingId") Long bookingId,
            @RequestParam("approved") boolean approved
    ) {
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @PatchMapping(path = {"", "/"}, params = "!bookingId")
    public BookingResponse approveImplicit(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam("approved") boolean approved) {
        return bookingService.approveLatestWaiting(ownerId, approved);
    }


    @PatchMapping({"/{bookingId}/cancel", "/{bookingId}/cancel/"})
    public BookingResponse cancel(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @PathVariable Long bookingId) {
        return bookingService.cancelBooking(bookerId, bookingId);
    }

    @GetMapping({"/{bookingId}", "/{bookingId}/"})
    public BookingResponse getById(
            @RequestHeader("X-Sharer-User-Id") Long requesterId,
            @PathVariable Long bookingId) {
        return bookingService.getBookingById(requesterId, bookingId);
    }

    @GetMapping
    public List<BookingResponse> getForBooker(
            @RequestHeader("X-Sharer-User-Id") Long bookerId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsByBooker(bookerId, state);
    }

    @GetMapping({"/owner", "/owner/"})
    public List<BookingResponse> getForOwner(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getBookingsByOwner(ownerId, state);
    }
}


