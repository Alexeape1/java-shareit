package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.validation.DuplicatedDataException;
import ru.practicum.shareit.validation.NotFoundException;
import ru.practicum.shareit.validation.ValidationException;

import java.util.*;

@Component
@Slf4j
@Deprecated
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAllUser() {
        log.info("Запрос на получение всех пользователей. Количество: {}", users.size());
        return users.values();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User createUser(User user) {
        log.info("Попытка создания пользователя: {}", user.getName());

        validateUser(user);
        if (existsByEmail(user.getEmail())) {
            throw new DuplicatedDataException("Email " + user.getEmail() + " уже используется");
        }
        user.setId(getNextId());

        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен: {}", user.getName());
        return user;
    }

    public User updateUser(User newUser) {
        log.info("Редактирование пользователя: {}", newUser.getName());

        if (newUser.getId() == null) {
            log.warn("id не указан");
            throw new ValidationException("Id должен быть указан");
        }

        User existingUser = users.get(newUser.getId());
        if (existingUser == null) {
            throw new NotFoundException("User с id = " + newUser.getId() + " не найден");
        }

        if (newUser.getEmail() != null && !newUser.getEmail().equals(existingUser.getEmail())) {
            if (existsByEmail(newUser.getEmail())) {
                throw new DuplicatedDataException("Email " + newUser.getEmail() + " уже используется");
            }
            existingUser.setEmail(newUser.getEmail());
        }

        if (newUser.getName() != null) {
            existingUser.setName(newUser.getName());
        }

        log.info("Пользователь с id = {} успешно обновлён", newUser.getId());
        return existingUser;
    }

    private void validateUser(User user) {
        if (user.getName() == null) {
            log.warn("Ошибка валидации: name не может быть пустым");
            throw new ValidationException("Имя не может быть пустым");
        }
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: email не указан или не содержит @");
            throw new ValidationException("Email должен быть указан и содержать @");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    private void checkEmailUniqueness(String email, Long excludeUserId) {
        boolean emailExists = users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email) && !user.getId().equals(excludeUserId));

        if (emailExists) {
            throw new DuplicatedDataException("Email " + email + " уже используется");
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Удаление пользователя с id: {}", id);
        User removed = users.remove(id);
        if (removed == null) {
            throw new NotFoundException("User с id = " + id + " не найден");
        }
        log.info("Пользователь удалён");
    }
}
