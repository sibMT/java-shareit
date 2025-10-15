package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerGatewayTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    ItemRequestClient client;

    @Test
    void getAll_invalidFrom_returns400() throws Exception {
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_valid_returns200_andCallsClient() throws Exception {
        when(client.getAll(any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(client).getAll(any(), anyInt(), anyInt());
    }

    @Test
    void create_valid_returns200_andCallsClient() throws Exception {
        when(client.create(any(), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"need drill\"}"))
                .andExpect(status().isOk());

        verify(client).create(any(), any());
    }
}

