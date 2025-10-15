package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerGatewayTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    BookingClient client;

    @Test
    void post_shouldReturn400_whenEndNotAfterStart() throws Exception {
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":1," +
                                "\"start\":\"2025-01-01T10:00:00\"," +
                                "\"end\":\"2025-01-01T10:00:00\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(client);
    }

    @Test
    void post_valid_returns200_andCallsClient() throws Exception {
        given(client.bookItem(anyLong(), any()))
                .willReturn(ResponseEntity.ok().build());

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":1," +
                                "\"start\":\"2025-01-01T10:00:00\"," +
                                "\"end\":\"2025-01-01T11:00:00\"}"))
                .andExpect(status().isOk());

        verify(client).bookItem(anyLong(), any());
    }

    @Test
    void approve_valid_callsClient() throws Exception {
        given(client.approve(anyLong(), anyLong(), anyBoolean()))
                .willReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/bookings/{bookingId}", 42)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(client).approve(anyLong(), anyLong(), eq(true));
    }

    @Test
    void get_bookings_forBooker_ok() throws Exception {
        given(client.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .willReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(client).getBookings(anyLong(), eq(BookingState.ALL), eq(0), eq(10));
    }

    @Test
    void get_bookings_forOwner_ok() throws Exception {
        given(client.getOwnerBookings(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .willReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "PAST")
                        .param("from", "5")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(client).getOwnerBookings(anyLong(), eq(BookingState.PAST), eq(5), eq(5));
    }

    @Test
    void get_single_booking_ok() throws Exception {
        given(client.getBooking(anyLong(), anyLong()))
                .willReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/{bookingId}", 100)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

        verify(client).getBooking(anyLong(), eq(100L));
    }
}



