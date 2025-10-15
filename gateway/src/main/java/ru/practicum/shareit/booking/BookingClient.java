package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> bookItem(long userId, Object body) {
        return post(API_PREFIX, userId, body);
    }

    public ResponseEntity<Object> getBooking(long userId, long bookingId) {
        return get(API_PREFIX + "/{bookingId}", userId, Map.of("bookingId", bookingId));
    }

    public ResponseEntity<Object> approve(long ownerId, long bookingId, boolean approved) {
        return patch(API_PREFIX + "/{bookingId}?approved={approved}",
                ownerId, Map.of("bookingId", bookingId, "approved", approved), null);
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, int from, int size) {
        return get(API_PREFIX + "?state={state}&from={from}&size={size}",
                userId, Map.of("state", state.name(), "from", from, "size", size));
    }

    public ResponseEntity<Object> getOwnerBookings(long ownerId, BookingState state, int from, int size) {
        return get(API_PREFIX + "/owner?state={state}&from={from}&size={size}",
                ownerId, Map.of("state", state.name(), "from", from, "size", size));
    }
}
