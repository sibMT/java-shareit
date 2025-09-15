package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserShortDto;

public class BookingMapper {

    private BookingMapper() {
    }

    public static Booking fromCreateRequest(BookingCreateRequest req, Item item, User booker) {
        return Booking.builder()
                .start(req.getStart())
                .end(req.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingResponse toDto(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getStart(),
                b.getEnd(),
                b.getStatus(),
                new ItemShortDto(b.getItem().getId(), b.getItem().getName()),
                new UserShortDto(b.getBooker().getId())
        );
    }

    public static BookingShortDto toShortDto(Booking b) {
        return new BookingShortDto(
                b.getId(),
                b.getStart(),
                b.getEnd(),
                b.getBooker().getId()
        );
    }
}
