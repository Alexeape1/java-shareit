package ru.practicum.shareit.server.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.booking.dto.BookingPostDto;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponseDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestBody BookingPostDto bookingDto) {
        BookingResponseDto createBooking = bookingService.create(userId, bookingDto);
        return ResponseEntity.ok(createBooking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable("bookingId") Long bookingId,
                                                      @RequestParam("approved") Boolean approved) {
        BookingResponseDto bookingResponseDto = bookingService.approve(userId, bookingId, approved);
        return ResponseEntity.ok(bookingResponseDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> findByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @PathVariable("bookingId") Long bookingId) {
        BookingResponseDto response = bookingService.getById(userId, bookingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                   @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getAllByBooker(userId, state));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return ResponseEntity.ok(bookingService.getAllByOwner(userId, state));
    }
}
