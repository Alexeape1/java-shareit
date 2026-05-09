package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Collection<ItemDto>> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.findAllByOwnerId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long id) {
        ItemDto findId = itemService.findById(userId, id);
        return ResponseEntity.ok(findId);
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemDto item) {
        ItemDto createItem = itemService.create(userId, item);
        return ResponseEntity.ok(createItem);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long id,
                                          @RequestBody ItemDto itemDto) {
        ItemDto updateItem = itemService.update(userId, id, itemDto);
        return ResponseEntity.ok(updateItem);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text) {
        List<ItemDto> searchItem = itemService.search(text);
        return ResponseEntity.ok(searchItem);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long itemId,
            @RequestBody Map<String, String> body) {

        String text = body.get("text");

        CommentDto comment = itemService.addComment(userId, itemId, text);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }
}
