package ru.practicum.shareit.server.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.service.ItemRequestService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;
import ru.practicum.shareit.server.validation.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private UserDto user;
    private UserDto otherUser;
    private ItemRequestCreateDto createDto;

    @BeforeEach
    void setUp() {
        user = userService.createUser(UserDto.builder()
                .name("Requester")
                .email("requester@test.com")
                .build());

        otherUser = userService.createUser(UserDto.builder()
                .name("Other")
                .email("other@test.com")
                .build());

        createDto = new ItemRequestCreateDto("Нужна аккумуляторная дрель");
    }

    @Test
    void createRequest_ShouldSaveAndReturnRequest() {
        ItemRequestDto saved = requestService.create(user.getId(), createDto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Нужна аккумуляторная дрель");
        assertThat(saved.getCreated()).isNotNull();
        assertThat(saved.getItems()).isEmpty();
    }

    @Test
    void getMyRequests_ShouldReturnUserRequests() {
        requestService.create(user.getId(), createDto);
        requestService.create(user.getId(), new ItemRequestCreateDto("Второй запрос"));

        var requests = requestService.getMyRequests(user.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Второй запрос"); // сортировка от новых
    }

    @Test
    void getMyRequests_ShouldReturnEmptyList_WhenNoRequests() {
        var requests = requestService.getMyRequests(user.getId());

        assertThat(requests).isEmpty();
    }

    @Test
    void getAllOtherRequests_ShouldNotIncludeUserOwnRequests() {
        requestService.create(user.getId(), createDto);
        requestService.create(otherUser.getId(), new ItemRequestCreateDto("Чужой запрос"));

        var requests = requestService.getAllOtherRequests(user.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getDescription()).isEqualTo("Чужой запрос");
    }

    @Test
    void getRequestById_ShouldThrowNotFound_WhenRequestNotExists() {
        assertThatThrownBy(() -> requestService.getRequestById(user.getId(), 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRequestById_AnyUserCanViewRequest() {
        ItemRequestDto request = requestService.create(user.getId(), createDto);

        ItemRequestDto found = requestService.getRequestById(otherUser.getId(), request.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(request.getId());
    }

    @Test
    void requestsAreSortedByCreatedDesc() throws InterruptedException {
        requestService.create(user.getId(), createDto);

        Thread.sleep(10); // небольшая задержка для разных временных меток
        requestService.create(user.getId(), new ItemRequestCreateDto("Новый запрос"));

        var requests = requestService.getMyRequests(user.getId());

        assertThat(requests).hasSize(2);
        assertThat(requests.get(0).getDescription()).isEqualTo("Новый запрос");
        assertThat(requests.get(1).getDescription()).isEqualTo("Нужна аккумуляторная дрель");
    }
}