package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.Create;
import ru.practicum.shareit.item.Update;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "имя не может быть пустым",groups = Create.class)
    private String name;
    @NotBlank(message = "описание не может быть пустым", groups = Create.class)
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Long ownerId;
    @Positive(groups = {Create.class, Update.class})
    private Long requestId;
}
