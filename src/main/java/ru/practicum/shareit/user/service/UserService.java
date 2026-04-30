package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import java.util.Collection;

 public interface UserService {

     Collection<UserDto> getAllUsers();

     UserDto getUserById(Long id);

     UserDto createUser(UserDto userDto);

     UserDto updateUser(Long id, UserDto newUser);

     void deleteUser(Long id);
 }
