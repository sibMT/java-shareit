package ru.practicum.shareit.item;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerGatewayTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    ItemClient client;

    @Test
    void create_validation_missingFields_returns400() throws Exception {
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content("{\"name\":\"\",\"description\":\"\",\"available\":null}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(client);
    }

    @Test
    void create_valid_returns200_andDelegates() throws Exception {
        given(client.create(anyLong(), any())).willReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "10")
                        .contentType("application/json")
                        .content("{\"name\":\"Drill\",\"description\":\"Power drill\",\"available\":true}"))
                .andExpect(status().isOk());

        verify(client).create(anyLong(), any());
    }
}


