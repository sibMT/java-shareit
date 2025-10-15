package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    void getByBooker_with_pagination_ok() throws Exception {
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
}

