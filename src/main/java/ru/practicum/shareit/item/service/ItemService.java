package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto findById(Long userId, Long itemId);

    Collection<ItemDto> findAllByOwnerId(Long userId);

    List<ItemDto> search(String text);

    void delete(Long userId, Long itemId);

}
