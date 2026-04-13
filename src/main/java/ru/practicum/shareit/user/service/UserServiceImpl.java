package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.validation.NotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserStorage userStorage;

    @Override
    public Collection<UserDto> getAllUsers() {
        log.info("Получение всех пользователей");
        return userStorage.findAllUser().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Получение пользователя с id: {}", id);
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("User с id = " + id + " не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {

            log.info("Создание пользователя: {}", userDto.getName());
            User user = UserMapper.toUser(userDto);
            User savedUser = userStorage.save(user);
            return UserMapper.toUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto newUser) {
        log.info("Обновление пользователя с id: {}", id);

        User userForUpdate = User.builder()
                .id(id)
                .name(newUser.getName())
                .email(newUser.getEmail())
                .build();

        User updatedUser = userStorage.updateUser(userForUpdate);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id: {}", id);
        userStorage.deleteById(id);
    }
}
