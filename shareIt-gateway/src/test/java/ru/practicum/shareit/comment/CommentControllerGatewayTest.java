package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerGatewayTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    CommentClient client;

    @Test
    void addComment_blankText_returns400() throws Exception {
        mvc.perform(post("/items/{itemId}/comment", 10)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(client);
    }

    @Test
    void addComment_valid_returns200_andCallsClient() throws Exception {
        when(client.addComment(any(), any(), any())).thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items/{itemId}/comment", 10)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"Nice!\"}"))
                .andExpect(status().isOk());

        verify(client).addComment(any(), any(), any());
    }
}

