package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.validation.NotFoundException;
import ru.practicum.shareit.validation.ValidationException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Добавление нового item для пользователя id={}", userId);

        User owner = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        validateItem(itemDto);

        Item item = itemMapper.toEntity(itemDto, owner);
        Item savedItem = itemStorage.save(item);

        log.info("Item успешно добавлен с id={}", savedItem.getId());
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Обновление item id={} для пользователя id={}", itemId, userId);

        Item existingItem = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id = " + itemId + " не найден"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        itemMapper.updateEntity(existingItem, itemDto);

        Item updatedItem = itemStorage.update(existingItem);
        log.info("Item успешно обновлен");
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        log.info("Получение item id={}", itemId);

        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id = " + itemId + " не найден"));

        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> findAllByOwnerId(Long userId) {
        log.info("Получение всех вещей пользователя id={}", userId);

        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        return itemStorage.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("Поиск вещей по тексту: {}", text);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemStorage.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId, Long itemId) {
        log.info("Удаление item id={} пользователем id={}", itemId, userId);

        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id = " + itemId + " не найден"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Пользователь не является владельцем вещи");
        }

        itemStorage.deleteById(itemId);
        log.info("Item удален");
    }

    private void validateItem(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Статус доступности должен быть указан");
        }
    }
}
