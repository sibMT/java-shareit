package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerUnknownStateTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    BookingService service;

    @Test
    void getBookings_unknownState_400() throws Exception {
        when(service.getBookingsByBooker(1L, "SOMETHING"))
                .thenThrow(new IllegalArgumentException("bad state"));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "SOMETHING"))
                .andExpect(status().isBadRequest());
    }
}

