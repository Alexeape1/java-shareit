package ru.practicum.shareit.gateway.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.item.dto.CommentRequestDto;
import ru.practicum.shareit.gateway.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemGatewayController {

    private final RestTemplate restTemplate;

    @Value("${server.url}")
    private String serverUrl;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Gateway: GET /items for user {}", userId);
        return restTemplate.getForEntity(serverUrl + "/items?userId={userId}", Object.class, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId) {
        log.info("Gateway: GET /items/{} for user {}", itemId, userId);
        return restTemplate.getForEntity(serverUrl + "/items/{itemId}?userId={userId}",
                Object.class, itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Gateway: POST /items for user {}", userId);
        return restTemplate.postForEntity(serverUrl + "/items?userId={userId}", itemDto, Object.class, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Gateway: PATCH /items/{} for user {}", itemId, userId);
        org.springframework.http.HttpEntity<ItemDto> entity =
                new org.springframework.http.HttpEntity<>(itemDto);
        return restTemplate.exchange(
                serverUrl + "/items/{itemId}?userId={userId}",
                org.springframework.http.HttpMethod.PATCH,
                entity,
                Object.class,
                itemId, userId
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text) {
        log.info("Gateway: GET /items/search?text={}", text);
        return restTemplate.getForEntity(serverUrl + "/items/search?text={text}", Object.class, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentRequestDto commentDto) {
        log.info("Gateway: POST /items/{}/comment for user {}", itemId, userId);
        return restTemplate.postForEntity(
                serverUrl + "/items/{itemId}/comment?userId={userId}",
                commentDto, Object.class, itemId, userId);
    }
}