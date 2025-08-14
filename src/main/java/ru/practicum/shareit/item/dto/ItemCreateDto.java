package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateDto {
    @NotBlank(message = "имя не может быть пустым")
    private String name;
    @NotBlank(message = "описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}
