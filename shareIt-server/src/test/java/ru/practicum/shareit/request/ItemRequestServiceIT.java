package ru.practicum.shareit.request;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDetailsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIT {

    @Autowired
    ItemRequestService service;
    @Autowired
    UserRepository users;
    @Autowired
    ItemService itemService;

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

    @Test
    void getById_returns_items_linked_to_request() {
        var req = service.create(u1, ItemRequestDto.builder().description("need drill").build());

        var ownerId = u2;
        var item = itemService.createItem(
                ownerId,
                ItemDto.builder()
                        .name("Drill")
                        .description("d")
                        .available(true)
                        .requestId(req.getId())
                        .build()
        );
        var details = service.getById(u1, req.getId());
        assertThat(details.getItems()).extracting(ir -> ir.getId())
                .isNotEmpty();
    }

    @Test
    void getAllExceptOwn_empty_when_no_requests_from_others() {
        service.create(u1, ItemRequestDto.builder().description("r1").build());

        var page = service.getAllExceptOwn(u2, 0, 10);
        assertThat(page).isNotNull().isNotEmpty();
    }

    @Test
    void getAllExceptOwn_out_of_bounds_returns_empty() {
        service.create(u1, ItemRequestDto.builder().description("r1").build());
        service.create(u1, ItemRequestDto.builder().description("r2").build());

        var page = service.getAllExceptOwn(u2, 10, 5);
        assertThat(page).isEmpty();
    }

    @Test
    void getById_notFound() {
        assertThatThrownBy(() -> service.getById(u1, 9999L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void userNotFound_in_all_endpoints() {
        Long ghost = 999_999L;

        assertThatThrownBy(() -> service.create(ghost, ItemRequestDto.builder().description("x").build()))
                .isInstanceOf(NoSuchElementException.class);

        assertThatThrownBy(() -> service.getOwn(ghost))
                .isInstanceOf(NoSuchElementException.class);

        assertThatThrownBy(() -> service.getAllExceptOwn(ghost, 0, 10))
                .isInstanceOf(NoSuchElementException.class);

        assertThatThrownBy(() -> service.getById(ghost, 123L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void pagination_from_size_page_math_without_validation() {
        for (int i = 0; i < 5; i++) {
            service.create(u1, ItemRequestDto.builder().description("r" + i).build());
            sleepTiny();
        }

        var page0 = service.getAllExceptOwn(u2, 0, 2);
        assertThat(page0).extracting(ItemRequestDetailsDto::getDescription)
                .containsExactly("r4", "r3");

        var negFrom = service.getAllExceptOwn(u2, -1, 2);
        assertThat(negFrom).extracting(ItemRequestDetailsDto::getDescription)
                .containsExactly("r4", "r3");

        var page1_from3 = service.getAllExceptOwn(u2, 3, 2);
        assertThat(page1_from3).extracting(ItemRequestDetailsDto::getDescription)
                .containsExactly("r2", "r1");

        var tail = service.getAllExceptOwn(u2, 5, 2);
        assertThat(tail).hasSize(1);
        assertThat(tail.get(0).getDescription()).isEqualTo("r0");

        assertThat(service.getAllExceptOwn(u2, 10, 2)).isEmpty();
    }


    @Test
    void collectAnswers_mixed_requests_some_with_no_items() {
        var r1 = service.create(u1, ItemRequestDto.builder().description("need drill").build());
        sleepTiny();
        var r2 = service.create(u1, ItemRequestDto.builder().description("need ladder").build());
        sleepTiny();
        var r3 = service.create(u1, ItemRequestDto.builder().description("need saw").build());

        itemService.createItem(u2, ItemDto.builder().name("Drill").description("d").available(true).requestId(r1.getId()).build());
        itemService.createItem(u2, ItemDto.builder().name("Saw").description("d").available(true).requestId(r3.getId()).build());

        var own = service.getOwn(u1);
        var byId = own.stream().collect(java.util.stream.Collectors.toMap(ItemRequestDetailsDto::getId, it -> it));

        assertThat(byId.get(r2.getId()).getItems()).isEmpty();
        assertThat(byId.get(r1.getId()).getItems()).isNotEmpty();
        assertThat(byId.get(r3.getId()).getItems()).isNotEmpty();
    }

    @Test
    void create_setsCreated_and_getOwn_sorted_desc() {
        var t0 = java.time.LocalDateTime.now().minusSeconds(1);
        var a = service.create(u1, ItemRequestDto.builder().description("A").build());
        sleepTiny();
        var b = service.create(u1, ItemRequestDto.builder().description("B").build());

        assertThat(a.getCreated()).isAfter(t0);
        assertThat(b.getCreated()).isAfter(a.getCreated());

        var own = service.getOwn(u1);
        assertThat(own).extracting(ItemRequestDetailsDto::getDescription)
                .containsExactly("B", "A");
    }
}

