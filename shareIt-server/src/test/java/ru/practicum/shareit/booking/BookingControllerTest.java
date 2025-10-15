package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    BookingService service;

    @Test
    void getByBooker_with_pagination() throws Exception {
        Mockito.when(service.getBookingsByBooker(eq(5L), eq("ALL")))
                .thenReturn(List.of(
                        new BookingResponse(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), BookingStatus.APPROVED, null, null),
                        new BookingResponse(2L, LocalDateTime.now(), LocalDateTime.now().plusHours(2), BookingStatus.WAITING, null, null)
                ));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "5")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void create() throws Exception {
        var start = LocalDateTime.now().plusDays(1);
        var end = start.plusDays(1);

        Mockito.when(service.createBooking(eq(1L), any()))
                .thenReturn(BookingResponse.builder().id(100L).status(BookingStatus.WAITING).build());

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":10,\"start\":\"" + start + "\",\"end\":\"" + end + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void approve() throws Exception {
        Mockito.when(service.approveBooking(1L, 5L, true))
                .thenReturn(BookingResponse.builder().id(5L).status(BookingStatus.APPROVED).build());

        mvc.perform(patch("/bookings/{id}", 5).param("approved", "true")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}

