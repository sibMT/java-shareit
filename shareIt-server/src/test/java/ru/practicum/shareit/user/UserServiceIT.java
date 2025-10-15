package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIT {

    @Autowired
    UserService service;

    @Test
    void create_update_get_delete_ok() {
        var created = service.createUser(new UserDto(null, "Max", "max@.com"));
        assertThat(created.getId()).isNotNull();

        var updated = service.updateUser(created.getId(), new UserDto(null, "Maxim", null));
        assertThat(updated.getName()).isEqualTo("Maxim");

        var got = service.getUserById(created.getId());
        assertThat(got.getEmail()).isEqualTo("max@.com");

        service.deleteUser(created.getId());
        assertThatThrownBy(() -> service.getUserById(created.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void uniqueEmail_violation() {
        service.createUser(new UserDto(null, "A", "dup@.com"));
        assertThatThrownBy(() -> service.createUser(new UserDto(null, "B", "dup@.com")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
