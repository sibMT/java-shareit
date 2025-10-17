package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerViewDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    ItemService service;
    @MockBean
    CommentService commentService;

    @Test
    void create() throws Exception {
        Mockito.when(service.createItem(eq(1L), any()))
                .thenReturn(ItemDto.builder().id(10L).name("Drill").available(true).build());

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Drill\",\"description\":\"d\",\"available\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void search_blank_returnsEmpty() throws Exception {
        mvc.perform(get("/items/search").param("text", ""))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getById() throws Exception {
        Mockito.when(service.getItemById(1L, 10L))
                .thenReturn(ItemOwnerViewDto.builder().id(10L).name("D").available(true).build());

        mvc.perform(get("/items/{id}", 10).header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void search() throws Exception {
        Mockito.when(service.searchItem("drill"))
                .thenReturn(List.of(ItemDto.builder().id(1L).name("Drill").available(true).build()));

        mvc.perform(get("/items/search").param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drill"));
    }

    @Test
    void update_forbidden_whenNotOwner() throws Exception {
        Mockito.when(service.updateItem(eq(2L), eq(10L), any()))
                .thenThrow(new SecurityException("forbidden"));

        mvc.perform(patch("/items/{id}", 10)
                        .header("X-Sharer-User-Id", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Hack\"}"))
                .andExpect(status().isForbidden());
    }
}
