package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceRequesterNotFoundTest {

    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    ItemRequestMapper itemRequestMapper;

    @InjectMocks
    ItemRequestServiceImpl service;

    @Test
    void create_requesterNotFound_throws() {
        when(userRepository.findById(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.create(55L, ItemRequestDto.builder().description("need drill").build())
        ).isInstanceOf(NoSuchElementException.class);
    }
}

