package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    JacksonTester<UserDto> json;

    @Test
    void serialize_and_deserialize() throws Exception {
        var dto = new UserDto(7L, "Max", "max@ex.com");

        var content = json.write(dto);
        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(7);
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo("Max");
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo("max@ex.com");

        var parsed = json.parseObject(content.getJson());
        assertThat(parsed.getId()).isEqualTo(7L);
        assertThat(parsed.getName()).isEqualTo("Max");
        assertThat(parsed.getEmail()).isEqualTo("max@ex.com");
    }
}
