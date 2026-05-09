package ru.practicum.shareit.server.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;
import ru.practicum.shareit.server.validation.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private UserDto owner;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        owner = userService.createUser(UserDto.builder()
                .name("Owner")
                .email("owner@test.com")
                .build());

        itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
    }

    @Test
    void createItem_ShouldSaveAndReturnItem() {
        ItemDto saved = itemService.create(owner.getId(), itemDto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test Item");
        assertThat(saved.getAvailable()).isTrue();
    }

    @Test
    void getItemById_ShouldReturnItem() {
        ItemDto saved = itemService.create(owner.getId(), itemDto);
        ItemDto found = itemService.findById(owner.getId(), saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(saved.getId());
    }

    @Test
    void getItemById_ShouldThrowNotFound_WhenNotExists() {
        assertThatThrownBy(() -> itemService.findById(owner.getId(), 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateItem_ShouldUpdateFields() {
        ItemDto saved = itemService.create(owner.getId(), itemDto);

        ItemDto update = ItemDto.builder()
                .name("Updated Name")
                .description("Updated Description")
                .available(false)
                .build();

        ItemDto updated = itemService.update(owner.getId(), saved.getId(), update);

        assertThat(updated.getName()).isEqualTo("Updated Name");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
        assertThat(updated.getAvailable()).isFalse();
    }

    @Test
    void findAllByOwnerId_ShouldReturnAllItems() {
        itemService.create(owner.getId(), itemDto);
        itemService.create(owner.getId(), ItemDto.builder()
                .name("Second Item")
                .description("Second Description")
                .available(true)
                .build());

        var items = itemService.findAllByOwnerId(owner.getId());

        assertThat(items).hasSize(2);
    }

    @Test
    void search_ShouldReturnAvailableItems() {
        itemService.create(owner.getId(), itemDto);

        ItemDto unavailableItem = ItemDto.builder()
                .name("Unavailable Item")
                .description("Test")
                .available(false)
                .build();
        itemService.create(owner.getId(), unavailableItem);

        var results = itemService.search("Test");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAvailable()).isTrue();
    }

    @Test
    void deleteItem_ShouldRemoveItem() {
        ItemDto saved = itemService.create(owner.getId(), itemDto);
        itemService.delete(owner.getId(), saved.getId());

        assertThatThrownBy(() -> itemService.findById(owner.getId(), saved.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}