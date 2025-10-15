package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ItemMapperTest {
    private final ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void toDto_and_back() {
        Item item = new Item(5L, "Drill", "d", true, new User(1L, "O", "o@ex.com"), null);

        ItemDto dto = mapper.toItemDto(item);
        assertThat(dto.getId()).isEqualTo(5L);
        assertThat(dto.getOwnerId()).isEqualTo(1L);

        User owner = new User(1L, "O", "o@ex.com");

        Item back = mapper.toNewEntity(dto, owner, null);
        assertThat(back.getName()).isEqualTo("Drill");
        assertThat(back.getOwner()).isNotNull();
        assertThat(back.getOwner().getId()).isEqualTo(1L);
        assertThat(back.getRequest()).isNull();
    }
}
