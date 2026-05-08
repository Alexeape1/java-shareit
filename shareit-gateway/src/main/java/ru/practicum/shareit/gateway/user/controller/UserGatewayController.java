package ru.practicum.shareit.gateway.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.gateway.user.dto.UserDto;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserGatewayController {

    private final RestTemplate restTemplate;

    @Value("${server.url}")
    private String serverUrl;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Gateway: GET /users");
        return restTemplate.getForEntity(serverUrl + "/users", Object.class);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Gateway: GET /users/{}", userId);
        return restTemplate.getForEntity(serverUrl + "/users/{userId}", Object.class, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Gateway: POST /users, email={}", userDto.getEmail());
        return restTemplate.postForEntity(serverUrl + "/users", userDto, Object.class);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("Gateway: PATCH /users/{}", id);
        org.springframework.http.HttpEntity<UserDto> entity =
                new org.springframework.http.HttpEntity<>(userDto);
        return restTemplate.exchange(
                serverUrl + "/users/{id}",
                org.springframework.http.HttpMethod.PATCH,
                entity,
                Object.class,
                id
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Gateway: DELETE /users/{}", id);
        restTemplate.delete(serverUrl + "/users/{id}", id);
        return ResponseEntity.ok().build();
    }
}