package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceUpdateForbiddenTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemMapper itemMapper;
    @Mock
    ru.practicum.shareit.user.UserRepository userRepository;

    @InjectMocks
    ItemServiceImpl service;

    @Test
    void update_notOwner_forbidden() {
        var owner = new User(1L, "Owner", "o@ex.com");
        var otherId = 2L;
        var item = new Item(10L, "Drill", "d", true, owner, null);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThatThrownBy(() ->
                service.updateItem(otherId, 10L, ItemDto.builder().name("x").build())
        ).isInstanceOf(SecurityException.class);
    }
}
