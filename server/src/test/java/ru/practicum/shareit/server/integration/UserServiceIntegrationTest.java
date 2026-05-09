package ru.practicum.shareit.server.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;
import ru.practicum.shareit.server.validation.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        UserDto saved = userService.createUser(userDto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test User");
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        UserDto saved = userService.createUser(userDto);
        UserDto found = userService.getUserById(saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
    }

    @Test
    void getUserById_ShouldThrowNotFound_WhenNotExists() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void updateUser_ShouldUpdateNameAndEmail() {
        UserDto saved = userService.createUser(userDto);

        UserDto update = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        UserDto updated = userService.updateUser(saved.getId(), update);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void deleteUser_ShouldRemoveUser() {
        UserDto saved = userService.createUser(userDto);
        userService.deleteUser(saved.getId());

        assertThatThrownBy(() -> userService.getUserById(saved.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}