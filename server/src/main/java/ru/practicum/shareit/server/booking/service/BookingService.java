package ru.practicum.shareit.server.booking.service;

import ru.practicum.shareit.server.booking.dto.BookingPostDto;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto create(Long userId, BookingPostDto booking);

    BookingResponseDto approve(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto getById(Long userId, Long bookingId);

    List<BookingResponseDto> getAllByBooker(Long userId, String state);

    List<BookingResponseDto> getAllByOwner(Long userId, String state);

}
