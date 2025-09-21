package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(Long bookerId, BookingCreateRequest request);

    BookingResponse approveBooking(Long ownerId, Long bookingId, boolean approved);

    BookingResponse getBookingById(Long requesterId, Long bookingId);

    List<BookingResponse> getBookingsByBooker(Long bookerId, String state);

    List<BookingResponse> getBookingsByOwner(Long ownerId, String state);

    BookingResponse cancelBooking(Long bookerId, Long bookingId);

    BookingResponse approveLatestWaiting(Long ownerId, boolean approved);
}
