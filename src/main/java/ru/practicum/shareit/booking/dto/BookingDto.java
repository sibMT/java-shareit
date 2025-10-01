package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.Create;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(message = "Дата начала обязательна", groups = Create.class)
    private LocalDateTime start;
    @NotNull(message = "Дата окончания обязательна", groups = Create.class)
    private LocalDateTime end;
    private BookingStatus status;
    @NotNull(message = "itemId обязателен", groups = Create.class)
    private Long itemId;
    private Long bookerId;
}
