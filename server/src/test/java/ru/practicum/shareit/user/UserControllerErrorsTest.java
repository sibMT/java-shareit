package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerErrorsTest {

    @Autowired MockMvc mvc;
    @MockBean UserService service;

    @Test
    void getById_notFound_404() throws Exception {
        when(service.getUserById(999L)).thenThrow(new NoSuchElementException("not found"));

        mvc.perform(get("/users/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_duplicateEmail_409() throws Exception {
        when(service.createUser(any(UserDto.class)))
                .thenThrow(new DataIntegrityViolationException("dup"));

        mvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Max\",\"email\":\"dup@ex.com\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void update_duplicateEmail_409() throws Exception {
        when(service.updateUser(any(Long.class), any(UserDto.class)))
                .thenThrow(new DataIntegrityViolationException("dup"));

        mvc.perform(patch("/users/{id}", 1)
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\":\"dup@ex.com\"}"))
                .andExpect(status().isConflict());
    }
}
