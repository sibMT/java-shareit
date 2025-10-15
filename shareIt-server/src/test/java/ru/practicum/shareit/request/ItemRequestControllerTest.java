package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDetailsDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    ItemRequestService service;

    @Test
    void create() throws Exception {
        Mockito.when(service.create(eq(1L), any())).thenReturn(
                ItemRequestDetailsDto.builder().id(100L).description("need").build()
        );

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"need\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void getOwn() throws Exception {
        Mockito.when(service.getOwn(1L)).thenReturn(List.of(
                ItemRequestDetailsDto.builder().id(10L).description("need A").build(),
                ItemRequestDetailsDto.builder().id(11L).description("need B").build()
        ));

        mvc.perform(get("/requests").header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[1].description").value("need B"));
    }

    @Test
    void getAllExceptOwn_with_from_size() throws Exception {
        Mockito.when(service.getAllExceptOwn(2L, 0, 2)).thenReturn(List.of(
                ItemRequestDetailsDto.builder().id(20L).description("r2").build(),
                ItemRequestDetailsDto.builder().id(21L).description("r1").build()
        ));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "2")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("r2"));
    }

    @Test
    void getById() throws Exception {
        Mockito.when(service.getById(2L, 99L)).thenReturn(
                ItemRequestDetailsDto.builder().id(99L).description("need").items(List.of()).build()
        );

        mvc.perform(get("/requests/{requestId}", 99).header("X-Sharer-User-Id", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.items").isArray());
    }
}

