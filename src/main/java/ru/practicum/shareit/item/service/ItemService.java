package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    /**
     * Создание новой вещи
     *
     * @param userId идентификатор владельца вещи (из заголовка X-Sharer-User-Id)
     * @param item   данные для создания вещи (название, описание, статус доступности)
     * @return созданная вещь с присвоенным ID
     * @throws ru.practicum.shareit.validation.NotFoundException   если пользователь с userId не найден
     * @throws ru.practicum.shareit.validation.ValidationException если поля name, description или available некорректны
     */
    ItemDto create(Long userId, ItemDto item);

    /**
     * Обновление существующей вещи
     *
     * @param userId  идентификатор владельца вещи (должен совпадать с владельцем)
     * @param itemId  идентификатор обновляемой вещи
     * @param itemDto данные для обновления (только переданные поля будут обновлены)
     * @return обновленная вещь
     * @throws ru.practicum.shareit.validation.NotFoundException   если вещь с id не найдена
     * @throws ru.practicum.shareit.validation.ValidationException если пользователь не является владельцем
     */
    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    /**
     * Получение вещи по идентификатору
     *
     * @param userId идентификатор пользователя (для аудита)
     * @param itemId идентификатор вещи
     * @return найденная вещь
     * @throws ru.practicum.shareit.validation.NotFoundException если вещь не найдена
     */
    ItemDto findById(Long userId, Long itemId);

    /**
     * Получение всех вещей пользователя (владельца)
     *
     * @param userId идентификатор владельца
     * @return коллекция вещей пользователя
     * @throws ru.practicum.shareit.validation.NotFoundException если пользователь не найден
     */
    Collection<ItemDto> findAllByOwnerId(Long userId);

    /**
     * Поиск доступных вещей по тексту (в названии или описании)
     * Возвращаются только вещи с available = true
     *
     * @param text текст для поиска
     * @return список вещей, соответствующих критериям поиска
     */
    List<ItemDto> search(String text);

    /**
     * Удаление вещи
     *
     * @param userId идентификатор владельца вещи
     * @param itemId идентификатор удаляемой вещи
     * @throws ru.practicum.shareit.validation.NotFoundException   если вещь не найдена
     * @throws ru.practicum.shareit.validation.ValidationException если пользователь не является владельцем
     */
    void delete(Long userId, Long itemId);

    CommentDto addComment(Long userId, Long itemId, String text);

}
