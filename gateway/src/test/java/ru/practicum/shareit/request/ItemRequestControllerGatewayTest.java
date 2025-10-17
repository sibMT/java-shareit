package ru.practicum.shareit.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
    @DisplayName("GET /requests/all?from=-1 -> 400")
    void getAll_shouldReturn400_onInvalidFrom() throws Exception {
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(client);
    }

    @Test
    @DisplayName("POST /requests — валидный -> 200, делегирование в client.create")
    void create_valid_200_andDelegates() throws Exception {
        given(client.create(anyLong(), any())).willReturn(ResponseEntity.ok().build());

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "7")
                        .contentType("application/json")
                        .content("{\"description\":\"Need a drill\"}"))
                .andExpect(status().isOk());

        verify(client).create(anyLong(), any());
    }

    @Test
    @DisplayName("GET /requests/all — валидная пагинация -> 200, делегирование в client.getAll")
    void getAll_valid_200_andDelegates() throws Exception {
        given(client.getAll(anyLong(), anyInt(), anyInt())).willReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "9")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(client).getAll(anyLong(), anyInt(), anyInt());
    }
}


