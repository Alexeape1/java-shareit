package ru.practicum.shareit.server.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.server.booking.dto.BookingPostDto;
import ru.practicum.shareit.server.booking.dto.BookingResponseDto;
import ru.practicum.shareit.server.booking.Status;
import ru.practicum.shareit.server.item.dto.ItemDto;
import ru.practicum.shareit.server.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingPostDto> postJson;

    @Autowired
    private JacksonTester<BookingResponseDto> responseJson;

    @Test
    void serializeBookingPostDto_ShouldCreateValidJson() throws Exception {
        BookingPostDto dto = new BookingPostDto(
                LocalDateTime.of(2025, 1, 1, 10, 0),
                LocalDateTime.of(2025, 1, 5, 10, 0),
                1L
        );

        var result = postJson.write(dto);

        assertThat(result).hasJsonPathStringValue("@.start");
        assertThat(result).hasJsonPathStringValue("@.end");
        assertThat(result).extractingJsonPathNumberValue("@.itemId").isEqualTo(1);
    }

    @Test
    void serializeBookingResponseDto_ShouldCreateValidJson() throws Exception {
        BookingResponseDto dto = BookingResponseDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2025, 1, 1, 10, 0))
                .end(LocalDateTime.of(2025, 1, 5, 10, 0))
                .status(Status.APPROVED)
                .booker(UserDto.builder().id(1L).name("Booker").email("booker@test.com").build())
                .item(ItemDto.builder().id(1L).name("Item").build())
                .build();

        var result = responseJson.write(dto);

        assertThat(result).hasJsonPathNumberValue("@.id");
        assertThat(result).hasJsonPathStringValue("@.status");
        assertThat(result).extractingJsonPathStringValue("@.status").isEqualTo("APPROVED");
    }
}