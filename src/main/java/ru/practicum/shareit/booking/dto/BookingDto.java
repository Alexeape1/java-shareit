package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.Instant;

@Data
@AllArgsConstructor
public class BookingDto {
    private long id;
    private Instant start;
    private Instant end;
    private Item item;
    private User booker;
    private Status status;
}
