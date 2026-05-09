package ru.practicum.shareit.server.item.storage;

import ru.practicum.shareit.server.item.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Deprecated
public interface ItemStorage {

    Item save(Item item);

    Item update(Item item);

    Optional<Item> findById(Long id);

    Collection<Item> findAllByOwnerId(Long ownerId);

    List<Item> search(String text);

    void deleteById(Long id);
}
