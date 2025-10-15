package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    @NotNull(message = "itemId обязателен")
    private Long itemId;

    @NotNull(message = "Дата начала обязательна")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания обязательна")
    private LocalDateTime end;
}
