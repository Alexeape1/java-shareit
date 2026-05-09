package ru.practicum.shareit.server.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.booking.dto.BookingPostDto;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.item.service.ItemService;
import ru.practicum.shareit.server.user.dto.UserDto;
import ru.practicum.shareit.server.user.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private UserDto owner;
    private UserDto booker;
    private ItemDto item;
    private BookingPostDto bookingDto;

    @BeforeEach
    void setUp() {
        owner = userService.createUser(UserDto.builder()
                .name("Owner")
                .email("owner@test.com")
                .build());

        booker = userService.createUser(UserDto.builder()
                .name("Booker")
                .email("booker@test.com")
                .build());

        item = itemService.create(owner.getId(), ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build());

        bookingDto = new BookingPostDto(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item.getId()
        );
    }

    @Test
    void createBooking_ShouldSaveAndReturnBooking() {
        BookingResponseDto booking = bookingService.create(booker.getId(), bookingDto);

        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(ru.practicum.shareit.server.booking.Status.WAITING);
    }

    @Test
    void approveBooking_ShouldChangeStatusToApproved() {
        BookingResponseDto booking = bookingService.create(booker.getId(), bookingDto);
        BookingResponseDto approved = bookingService.approve(owner.getId(), booking.getId(), true);

        assertThat(approved.getStatus()).isEqualTo(ru.practicum.shareit.server.booking.Status.APPROVED);
    }
}