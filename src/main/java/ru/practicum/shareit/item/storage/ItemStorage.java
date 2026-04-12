package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Optional<Item> getById(Long id);

    Collection<Item> getAll();

    Collection<Item> getByOwner(Long ownerId);

    Collection<Item> search(String text);

    void delete(Long id);
}