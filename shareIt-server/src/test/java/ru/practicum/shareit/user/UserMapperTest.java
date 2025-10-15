package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void user_toDto_and_back() {
        User entity = new User(10L, "Max", "m@ex.com");

        UserDto dto = mapper.toUserDto(entity);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getEmail()).isEqualTo("m@ex.com");

        User back = mapper.toNewEntity(dto);
        assertThat(back).isNotNull();
        assertThat(back.getId()).isNull();
        assertThat(back.getName()).isEqualTo("Max");
        assertThat(back.getEmail()).isEqualTo("m@ex.com");
    }
}


