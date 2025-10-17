package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping
    public UserDto create(@RequestBody UserDto dto) {
        return service.createUser(dto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id,
                          @RequestBody UserDto dto) {
        return service.updateUser(id, dto);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        return service.getUserById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return service.getAllUsers();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteUser(id);
    }
}
