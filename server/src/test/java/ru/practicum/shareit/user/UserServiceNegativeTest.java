package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class UserServiceNegativeTest {

    UserRepository repo;
    UserMapper mapper;
    UserService service;

    @BeforeEach
    void setUp() {
        repo = mock(UserRepository.class);
        mapper = mock(UserMapper.class);
        service = new UserServiceImpl(repo, mapper);
    }

    @Test
    void update_notFound_throws() {
        when(repo.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateUser(123L, new UserDto(null, "X", null)))
                .isInstanceOf(NoSuchElementException.class);
        verify(repo, never()).save(any());
    }

    @Test
    void delete_notFound_throws() {
        when(repo.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteUser(77L))
                .isInstanceOf(NoSuchElementException.class);
        verify(repo, never()).deleteById(anyLong());
    }
}
