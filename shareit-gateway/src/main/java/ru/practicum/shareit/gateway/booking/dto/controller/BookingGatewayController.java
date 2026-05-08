package ru.practicum.shareit.gateway.booking.dto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.booking.dto.BookingPostDto;
import ru.practicum.shareit.gateway.booking.dto.BookingState;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingGatewayController {

    private final RestTemplate restTemplate;

    @Value("${server.url}")
    private String serverUrl;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody BookingPostDto bookingDto) {
        log.info("Gateway: POST /bookings for user {}", userId);
        return restTemplate.postForEntity(serverUrl + "/bookings?userId={userId}",
                bookingDto, Object.class, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        log.info("Gateway: PATCH /bookings/{}?approved={} for user {}", bookingId, approved, userId);
        return restTemplate.exchange(
                serverUrl + "/bookings/{bookingId}?approved={approved}&userId={userId}",
                org.springframework.http.HttpMethod.PATCH,
                null,
                Object.class,
                bookingId, approved, userId
        );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId) {
        log.info("Gateway: GET /bookings/{} for user {}", bookingId, userId);
        return restTemplate.getForEntity(serverUrl + "/bookings/{bookingId}?userId={userId}",
                Object.class, bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        log.info("Gateway: GET /bookings for user {} with state {}", userId, state);
        BookingState bookingState = BookingState.from(state);
        return restTemplate.getForEntity(serverUrl + "/bookings?userId={userId}&state={state}",
                Object.class, userId, bookingState.name());
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        log.info("Gateway: GET /bookings/owner for user {} with state {}", userId, state);
        BookingState bookingState = BookingState.from(state);
        return restTemplate.getForEntity(serverUrl + "/bookings/owner?userId={userId}&state={state}",
                Object.class, userId, bookingState.name());
    }
}