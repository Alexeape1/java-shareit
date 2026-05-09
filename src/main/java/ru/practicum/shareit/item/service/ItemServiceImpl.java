package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validation.NotFoundException;
import ru.practicum.shareit.validation.ValidationException;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Добавление нового item для пользователя id={}", userId);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        validateItem(itemDto);

        Item item = itemMapper.toEntity(itemDto, owner);
        Item savedItem = itemRepository.save(item);

        log.info("Item успешно добавлен с id={}", savedItem.getId());
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Обновление item id={} для пользователя id={}", itemId, userId);

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id = " + itemId + " не найден"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        itemMapper.updateEntity(existingItem, itemDto);

        Item updatedItem = itemRepository.save(existingItem);
        log.info("Item успешно обновлен");
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        log.info("Получение item id={}", itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id = " + itemId + " не найден"));

        ItemDto itemDto = itemMapper.toItemDto(item);

        List<CommentDto> comments = commentRepository.findAllByItemIdOrderByCreatedDesc(itemId)
                .stream()
                .map(itemMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            bookingRepository.findLastBookingByItemId(itemId, now)
                    .ifPresent(booking -> {
                        BookingShortDto lastBooking = BookingShortDto.builder()
                                .id(booking.getId())
                                .bookerId(booking.getBooker().getId())
                                .build();
                        itemDto.setLastBooking(lastBooking);
                    });

            bookingRepository.findNextBookingByItemId(itemId, now)
                    .ifPresent(booking -> {
                        BookingShortDto nextBooking = BookingShortDto.builder()
                                .id(booking.getId())
                                .bookerId(booking.getBooker().getId())
                                .build();
                        itemDto.setNextBooking(nextBooking);
                    });
        }

        return itemDto;
    }

    @Override
    public Collection<ItemDto> findAllByOwnerId(Long userId) {
        log.info("Получение всех вещей пользователя id={}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        log.info("Поиск вещей по тексту: {}", text);

        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long userId, Long itemId) {
        log.info("Удаление item id={} пользователем id={}", itemId, userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item с id = " + itemId + " не найден"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Пользователь не является владельцем вещи");
        }

        itemRepository.deleteById(itemId);
        log.info("Item удален");
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, String text) {
        log.info("Добавление комментария к вещи id={} от пользователя id={}", itemId, userId);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена"));

        boolean hasCompletedBooking = bookingRepository.existsCompletedBooking(
                userId, itemId, Status.APPROVED, LocalDateTime.now());

        if (!hasCompletedBooking) {
            throw new ValidationException("Пользователь может оставить комментарий только после завершения аренды вещи");
        }

        Comment comment = Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Комментарий успешно добавлен с id={}", savedComment.getId());

        return itemMapper.toCommentDto(savedComment);
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
