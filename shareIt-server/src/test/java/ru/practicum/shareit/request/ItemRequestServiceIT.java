package ru.practicum.shareit.request;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIT {

    @Autowired
    ItemRequestService service;
    @Autowired
    UserRepository users;

    Long u1, u2;

    @BeforeEach
    void setUp() {
        u1 = users.save(new User(null, "Anna", "Anna@.com")).getId();
        u2 = users.save(new User(null, "Ivan", "Ivan@.com")).getId();
    }

    @Test
    void create_and_getOwn_desc() {
        service.create(u1, ItemRequestDto.builder().description("need drill").build());
        sleepTiny();
        service.create(u1, ItemRequestDto.builder().description("need ladder").build());

        List<ItemRequestDetailsDto> own = service.getOwn(u1);
        assertThat(own).hasSize(2);
        assertThat(own.get(0).getDescription()).isEqualTo("need ladder");
        assertThat(own.get(1).getDescription()).isEqualTo("need drill");
        assertThat(own.get(0).getItems()).isEmpty();
    }

    @Test
    void getAllExceptOwn_pagination_from_size() {
        service.create(u1, ItemRequestDto.builder().description("r1").build());
        sleepTiny();
        service.create(u1, ItemRequestDto.builder().description("r2").build());
        sleepTiny();
        service.create(u1, ItemRequestDto.builder().description("r3").build());

        var page1 = service.getAllExceptOwn(u2, 0, 2);
        assertThat(page1).extracting(ItemRequestDetailsDto::getDescription)
                .containsExactly("r3", "r2");

        var page2 = service.getAllExceptOwn(u2, 2, 2);
        assertThat(page2).extracting(ItemRequestDetailsDto::getDescription)
                .containsExactly("r1");
    }

    private static void sleepTiny() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException ignore) {
        }
    }
}

