package ru.practicum.shareit.server.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.UserMapper;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.repository.UserRepository;
import ru.practicum.shareit.server.validation.NotFoundException;
import ru.practicum.shareit.server.validation.ValidationException;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> getAllUsers() {
        log.info("Получение всех пользователей");
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Получение пользователя с id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User с id = " + id + " не найден"));
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Создание пользователя: {}", userDto.getName());

        User user = userMapper.toUser(userDto);
        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

 @Override
 @Transactional
 public UserDto updateUser(Long id, UserDto newUser) {
     log.info("Обновление пользователя с id: {}", id);

     try {
         User user = userRepository.findById(id)
                 .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));

         if (newUser == null) {
             throw new ValidationException("Данные для обновления не могут быть пустыми");
         }

         if (newUser.getName() != null && !newUser.getName().isBlank()) {
             user.setName(newUser.getName());
         }

         if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
             userRepository.findByEmail(newUser.getEmail())
                     .ifPresent(existingUser -> {
                         if (!existingUser.getId().equals(id)) {
                             throw new RuntimeException("Email уже используется: " + newUser.getEmail());
                         }
                     });
             user.setEmail(newUser.getEmail());
         }

         User updatedUser = userRepository.save(user);

         UserDto result = userMapper.toUserDto(updatedUser);

         return result;
     } catch (Exception e) {
         throw e;
     }
 }
    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id: {}", id);
        userRepository.deleteById(id);
    }
}
