package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerGatewayTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    UserClient client;

    @Test
    void create_invalidEmail_returns400() throws Exception {
        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"name\":\"Max\",\"email\":\"not-an-email\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(client);
    }

    @Test
    void create_valid_returns200() throws Exception {
        given(client.create(any())).willReturn(ResponseEntity.ok().build());

        mvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"name\":\"Max\",\"email\":\"max@mail.com\"}"))
                .andExpect(status().isOk());

        verify(client).create(any());
    }

    @Test
    void patch_invalidEmail_returns400() throws Exception {
        mvc.perform(patch("/users/{id}", 5L)
                        .contentType("application/json")
                        .content("{\"email\":\"bad\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(client);
    }

    @Test
    void patch_valid_returns200() throws Exception {
        given(client.update(anyLong(), any())).willReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/users/{id}", 5L)
                        .contentType("application/json")
                        .content("{\"email\":\"good@mail.com\"}"))
                .andExpect(status().isOk());

        verify(client).update(anyLong(), any());
    }
}

