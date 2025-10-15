package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceIT {

    @Autowired
    ItemService service;
    @Autowired
    UserRepository users;

    Long ownerId;

    @BeforeEach
    void setUp() {
        ownerId = users.save(new User(null, "Owner", "o@ex.com")).getId();
    }

    @Test
    void create_update_get_search() {
        var created = service.createItem(ownerId,
                ItemDto.builder().name("Drill").description("Cordless").available(true).build());

        var updated = service.updateItem(ownerId, created.getId(),
                ItemDto.builder().description("Cordless 18V").build());
        assertThat(updated.getDescription()).isEqualTo("Cordless 18V");

        var items = service.getItemsByOwner(ownerId);
        assertThat(items).hasSize(1);

        var found = service.searchItem("drill");
        assertThat(found).extracting(ItemDto::getName).containsExactly("Drill");

        var byId = service.getItemById(ownerId, created.getId());
        assertThat(byId.getName()).isEqualTo("Drill");
    }

    @Test
    void update_byNotOwner_forbidden() {
        var it = service.createItem(ownerId,
                ItemDto.builder().name("Ladder").description("3m").available(true).build());
        Long other = users.save(new User(null, "U", "u@ex.com")).getId();
        assertThatThrownBy(() -> service.updateItem(other, it.getId(), ItemDto.builder().name("X").build()))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void search_blank_returnsEmpty() {
        assertThat(service.searchItem("")).isEmpty();
    }
}
