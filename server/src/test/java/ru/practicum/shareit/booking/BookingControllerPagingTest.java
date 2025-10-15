package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingResponse;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerPagingTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    BookingService service;

    @Test
    void listByBooker_fromBeyondSize_returnsEmpty() throws Exception {
        var list = java.util.List.of(
                BookingResponse.builder().id(1L).build(),
                BookingResponse.builder().id(2L).build(),
                BookingResponse.builder().id(3L).build()
        );
        org.mockito.Mockito.when(service.getBookingsByBooker(1L, "ALL")).thenReturn(list);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "10").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listByOwner_tailPage_isClipped() throws Exception {
        var list = java.util.List.of(
                BookingResponse.builder().id(1L).build(),
                BookingResponse.builder().id(2L).build(),
                BookingResponse.builder().id(3L).build(),
                BookingResponse.builder().id(4L).build()
        );
        org.mockito.Mockito.when(service.getBookingsByOwner(2L, "ALL")).thenReturn(list);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "2")
                        .param("state", "ALL")
                        .param("from", "3").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(4));
    }
}
