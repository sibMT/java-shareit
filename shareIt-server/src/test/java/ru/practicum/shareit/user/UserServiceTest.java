package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import ru.practicum.shareit.user.dto.UserDto;

class UserServiceTest {

    UserRepository repo;
    UserMapper mapper;
    UserService service;

    @BeforeEach
    void setUp() {
        repo = mock(UserRepository.class);
        mapper = mock(UserMapper.class);
        service = new UserServiceImpl(repo, mapper);

        when(mapper.toUserDto(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            return new UserDto(u.getId(), u.getName(), u.getEmail());
        });
        when(mapper.toNewEntity(any(UserDto.class))).thenAnswer(inv -> {
            UserDto d = inv.getArgument(0);
            return new User(null, d.getName(), d.getEmail());
        });
    }

    @Test
    void create() {
        when(repo.save(any(User.class)))
                .thenAnswer(inv -> {
                    User in = inv.getArgument(0);
                    return new User(1L, in.getName(), in.getEmail());
                });

        UserDto created = service.createUser(new UserDto(null, "A", "a@ex.com"));

        assertThat(created.getId()).isEqualTo(1L);
        assertThat(created.getName()).isEqualTo("A");
        assertThat(created.getEmail()).isEqualTo("a@ex.com");
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void getAll() {
        when(repo.findAll()).thenReturn(List.of(
                new User(1L, "A", "a@ex.com"),
                new User(2L, "B", "b@ex.com")
        ));

        List<UserDto> all = service.getAllUsers();

        assertThat(all).hasSize(2);
        assertThat(all).extracting(UserDto::getEmail)
                .containsExactlyInAnyOrder("a@ex.com", "b@ex.com");
        verify(repo).findAll();
    }

    @Test
    void getById() {
        when(repo.findById(1L)).thenReturn(Optional.of(new User(1L, "A", "a@ex.com")));

        UserDto dto = service.getUserById(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("A");
        verify(repo).findById(1L);
    }

    @Test
    void update() {
        User existing = new User(1L, "A", "a@ex.com");
        when(repo.findById(1L)).thenReturn(Optional.of(existing));

        doAnswer(inv -> {
            UserDto patch = inv.getArgument(0);
            User target = inv.getArgument(1);
            if (patch.getName() != null) target.setName(patch.getName());
            if (patch.getEmail() != null) target.setEmail(patch.getEmail());
            return null;
        }).when(mapper).updateEntity(any(UserDto.class), any(User.class));

        when(repo.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDto patch = new UserDto(null, "AA", null);
        UserDto updated = service.updateUser(1L, patch);

        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getName()).isEqualTo("AA");
        assertThat(updated.getEmail()).isEqualTo("a@ex.com");
        verify(repo).save(any(User.class));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("AA");
        assertThat(captor.getValue().getEmail()).isEqualTo("a@ex.com");
    }

    @Test
    void delete_ok() {
        User user = new User(1L, "A", "a@ex.com");

        when(repo.findById(anyLong()))
                .thenReturn(Optional.of(user), Optional.of(user));

        when(repo.existsById(1L)).thenReturn(true, false);

        doNothing().when(repo).deleteById(1L);

        service.deleteUser(1L);

        verify(repo).deleteById(1L);
    }
}





