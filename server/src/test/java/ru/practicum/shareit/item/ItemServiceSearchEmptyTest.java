package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ItemServiceSearchEmptyTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemMapper itemMapper;
    @Mock
    ru.practicum.shareit.user.UserRepository userRepository;

    @InjectMocks
    ItemServiceImpl service;

    @Test
    void search_blank_returnsEmptyAndDoesNotTouchRepo() {
        assertThat(service.searchItem("   ")).isEmpty();
        verifyNoInteractions(itemRepository);
    }
}

