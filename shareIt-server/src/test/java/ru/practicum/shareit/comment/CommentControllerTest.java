package ru.practicum.shareit.comment;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentCreateDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    CommentService service;

    @Test
    void addComment_ok() throws Exception {
        Mockito.when(service.addComment(eq(1L), eq(10L), any(CommentCreateDto.class)))
                .thenReturn(CommentDto.builder().id(5L).text("good").authorName("A").build());

        mvc.perform(post("/items/{itemId}/comment", 10)
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":"good"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.text").value("good"));
    }
}

