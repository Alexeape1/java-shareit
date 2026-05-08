package ru.practicum.shareit.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.server.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
             @RequestBody ItemRequestCreateDto createDto) {

        ItemRequestDto result = requestService.create(userId, createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getMyRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return ResponseEntity.ok(requestService.getMyRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllOtherRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId) {

        return ResponseEntity.ok(requestService.getAllOtherRequests(userId));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getRequestById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId) {

        return ResponseEntity.ok(requestService.getRequestById(userId, requestId));
    }
}