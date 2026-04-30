package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    ResponseEntity<BookingResponseDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody BookingPostDto bookingDto) {
        BookingResponseDto createBooking = bookingService.create(userId, bookingDto);
        return ResponseEntity.ok(createBooking);
    }

    @PatchMapping("/{bookingId}")
    ResponseEntity<BookingResponseDto> approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long bookingId,
                                               @RequestParam Boolean approved) {
        BookingResponseDto bookingResponseDto = bookingService.approve(userId, bookingId, approved);
        return ResponseEntity.ok(bookingResponseDto);
    }

    @GetMapping("/{bookingId}")
    ResponseEntity<BookingResponseDto> findByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable Long bookingId) {
        BookingResponseDto response = bookingService.getById(userId, bookingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                   @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getAllByBooker(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getAllByOwner(userId, state));
    }
}
