package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;



@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(Create.class) UserDto dto) {
        return userClient.create(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @RequestBody @Validated(Update.class) UserDto dto) {
        return userClient.update(id, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        return userClient.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return userClient.delete(id);
    }
}
