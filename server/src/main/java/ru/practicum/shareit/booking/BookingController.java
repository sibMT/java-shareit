package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingResponse create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                  @RequestBody BookingDto dto) {
        return service.createBooking(bookerId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponse approve(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                   @PathVariable Long bookingId,
                                   @RequestParam boolean approved) {
        return service.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse getById(@RequestHeader("X-Sharer-User-Id") Long requesterId,
                                   @PathVariable Long bookingId) {
        return service.getBookingById(requesterId, bookingId);
    }

    @GetMapping
    public List<BookingResponse> getByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        var list = service.getBookingsByBooker(userId, state);
        int start = Math.min(from, list.size());
        int end = Math.min(from + size, list.size());
        return list.subList(start, end);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                            @RequestParam(defaultValue = "ALL") String state,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        var list = service.getBookingsByOwner(ownerId, state);
        int start = Math.min(from, list.size());
        int end = Math.min(from + size, list.size());
        return list.subList(start, end);
    }
}
