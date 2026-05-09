package ru.practicum.shareit.server.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.server.booking.dto.BookingShortDto;
import ru.practicum.shareit.server.item.dto.CommentDto;
import ru.practicum.shareit.server.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void serialize_ShouldCreateValidJson() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Дрель аккумуляторная")
                .description("Мощная дрель с двумя аккумуляторами")
                .available(true)
                .requestId(5L)
                .lastBooking(BookingShortDto.builder()
                        .id(10L)
                        .bookerId(2L)
                        .build())
                .nextBooking(BookingShortDto.builder()
                        .id(11L)
                        .bookerId(3L)
                        .build())
                .comments(List.of(
                        CommentDto.builder()
                                .id(100L)
                                .text("Отличная дрель!")
                                .authorName("Иван")
                                .created(LocalDateTime.of(2025, 1, 15, 10, 30))
                                .build()
                ))
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("@.id");
        assertThat(result).hasJsonPathStringValue("@.name");
        assertThat(result).hasJsonPathStringValue("@.description");
        assertThat(result).hasJsonPathBooleanValue("@.available");
        assertThat(result).hasJsonPathNumberValue("@.requestId");

        assertThat(result).hasJsonPathNumberValue("@.lastBooking.id");
        assertThat(result).hasJsonPathNumberValue("@.lastBooking.bookerId");
        assertThat(result).hasJsonPathNumberValue("@.nextBooking.id");
        assertThat(result).hasJsonPathNumberValue("@.nextBooking.bookerId");

        assertThat(result).hasJsonPathArrayValue("@.comments");
        assertThat(result).hasJsonPathNumberValue("@.comments[0].id");
        assertThat(result).hasJsonPathStringValue("@.comments[0].text");
        assertThat(result).hasJsonPathStringValue("@.comments[0].authorName");

        // Проверка значений
        assertThat(result).extractingJsonPathNumberValue("@.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("@.name").isEqualTo("Дрель аккумуляторная");
        assertThat(result).extractingJsonPathBooleanValue("@.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("@.lastBooking.id").isEqualTo(10);
        assertThat(result).extractingJsonPathNumberValue("@.comments[0].id").isEqualTo(100);
        assertThat(result).extractingJsonPathStringValue("@.comments[0].text").isEqualTo("Отличная дрель!");
    }

    @Test
    void deserialize_ShouldCreateItemDto() throws Exception {
        String jsonContent = """
                {
                    "id": 1,
                    "name": "Дрель аккумуляторная",
                    "description": "Мощная дрель",
                    "available": true,
                    "requestId": 5
                }
                """;

        ItemDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Дрель аккумуляторная");
        assertThat(dto.getDescription()).isEqualTo("Мощная дрель");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getRequestId()).isEqualTo(5L);
    }

    @Test
    void serialize_WithoutOptionalFields_ShouldCreateValidJson() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Простая вещь")
                .description("Описание")
                .available(true)
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPathNumberValue("@.id");
        assertThat(result).hasJsonPathStringValue("@.name");
        assertThat(result).hasJsonPathStringValue("@.description");
        assertThat(result).hasJsonPathBooleanValue("@.available");

        assertThat(result).doesNotHaveJsonPathValue("@.requestId");
        assertThat(result).doesNotHaveJsonPathValue("@.lastBooking");
        assertThat(result).doesNotHaveJsonPathValue("@.nextBooking");
        assertThat(result).doesNotHaveJsonPathValue("@.comments");
    }

    @Test
    void serialize_WithNullBooking_ShouldHandleGracefully() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Описание")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .build();

        var result = json.write(dto);

        assertThat(result).doesNotHaveJsonPathValue("@.lastBooking");
        assertThat(result).doesNotHaveJsonPathValue("@.nextBooking");
    }
}