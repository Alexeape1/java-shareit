package ru.practicum.shareit.gateway.request.dto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestGatewayController {

    private final RestTemplate restTemplate;

    @Value("${server.url}")
    private String serverUrl;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestBody ItemRequestCreateDto createDto) {
        log.info("Gateway: POST /requests for user {}", userId);
        return restTemplate.postForEntity(serverUrl + "/requests?userId={userId}",
                createDto, Object.class, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getMyRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway: GET /requests for user {}", userId);
        return restTemplate.getForEntity(serverUrl + "/requests?userId={userId}",
                Object.class, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway: GET /requests/all for user {}", userId);
        return restTemplate.getForEntity(serverUrl + "/requests/all?userId={userId}",
                Object.class, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Gateway: GET /requests/{} for user {}", requestId, userId);
        return restTemplate.getForEntity(serverUrl + "/requests/{requestId}?userId={userId}",
                Object.class, requestId, userId);
    }
}