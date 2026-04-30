package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.validation.NotFoundException;
import ru.practicum.shareit.validation.ValidationException;

import java.util.*;
import java.util.stream.Collectors;

@Deprecated
@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item save(Item item) {

        if (item.getId() == null) {
            item.setId(getNextId());
        }
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item newItem) {
        log.info("Редактирование предмета: {}", newItem.getName());

        if (newItem.getId() == null) {
            log.warn("id не указан");
            throw new ValidationException("Id должен быть указан");
        }

        Item existingItem = items.get(newItem.getId());
        if (existingItem == null) {
            throw new NotFoundException("Item с id = " + newItem.getId() + " не найден");
        }

        if (newItem.getName() != null) {
            existingItem.setName(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            existingItem.setDescription(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            existingItem.setAvailable(newItem.getAvailable());
        }

        return existingItem;
    }

    @Override
    public Collection<Item> findAllByOwnerId(Long userId) {
        log.info("Получение всех вещей пользователя id={}", userId);

        return items.values().stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        log.info("Поиск вещей по тексту: {}", text);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)  // только доступные вещи
                .filter(item -> (item.getName() != null && item.getName().toLowerCase().contains(lowerText)) ||
                        (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerText)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        log.info("Удаление пользователя с id: {}", id);
        Item removed = items.remove(id);
        if (removed == null) {
            throw new NotFoundException("User с id = " + id + " не найден");
        }
        log.info("Пользователь удалён");
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
