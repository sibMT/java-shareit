package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(message = "Требуется имя",groups = Create.class)
    private String name;
    @NotBlank(groups = {Create.class})
    @Email(groups = {Update.class,Create.class})
    private String email;
}
