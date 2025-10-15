package ru.practicum.shareit.comment;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerGatewayTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    CommentClient client;

    @Test
    void addComment_blankText_400() throws Exception {
        mvc.perform(post("/items/{itemId}/comment", 10)
                        .header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content("{\"text\":\"\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(client);
    }

    @Test
    void addComment_valid_200_andDelegates() throws Exception {
        given(client.addComment(anyLong(), anyLong(), any())).willReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items/{itemId}/comment", 10L)
                        .header("X-Sharer-User-Id", "5")
                        .contentType("application/json")
                        .content("{\"text\":\"Nice thing!\"}"))
                .andExpect(status().isOk());

        verify(client).addComment(anyLong(), anyLong(), any());
    }
}


