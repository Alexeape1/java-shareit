package ru.practicum.shareit.server.user.storage;

import ru.practicum.shareit.server.user.User;

import java.util.Collection;
import java.util.Optional;

@Deprecated
public interface UserStorage {

    Collection<User> findAllUser();

    Optional<User> findById(Long id);

    User createUser(User user);

    User updateUser(User newUser);

    void deleteById(Long id);

    boolean existsByEmail(String email);
}