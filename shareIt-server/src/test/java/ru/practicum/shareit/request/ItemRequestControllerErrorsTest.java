package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.errors.ErrorHandler;

import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@Import(ErrorHandler.class)
public class ItemRequestControllerErrorsTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    ItemRequestService service;

    @Test
    void getById_notFound_maps404() throws Exception {
        Mockito.when(service.getById(1L, 999L))
                .thenThrow(new NoSuchElementException("not found"));

        mvc.perform(get("/requests/{requestId}", 999)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isNotFound());
    }

}
