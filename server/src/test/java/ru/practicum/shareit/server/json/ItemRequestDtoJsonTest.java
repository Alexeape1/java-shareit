package ru.practicum.shareit.server.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void deserialize_ShouldCreateItemRequestDto() throws Exception {
        String jsonContent = """
                {
                    "id": 1,
                    "description": "Нужна дрель",
                    "created": "2025-01-15T10:30:00",
                    "items": [
                        {
                            "id": 10,
                            "name": "Dewalt",
                            "ownerId": 5
                        }
                    ]
                }
                """;

        ItemRequestDto dto = json.parse(jsonContent).getObject();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDescription()).isEqualTo("Нужна дрель");
        assertThat(dto.getItems()).hasSize(1);
        assertThat(dto.getItems().get(0).getId()).isEqualTo(10L);
        assertThat(dto.getItems().get(0).getName()).isEqualTo("Dewalt");
        assertThat(dto.getItems().get(0).getOwnerId()).isEqualTo(5L);
    }

    @Test
    void serialize_WithEmptyItems_ShouldReturnEmptyArray() throws Exception {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.now())
                .items(List.of())
                .build();

        var result = json.write(dto);

        assertThat(result).hasJsonPathArrayValue("@.items");

        assertThat(result).extractingJsonPathArrayValue("@.items").isEmpty();

        assertThat(result).extractingJsonPathArrayValue("@.items").hasSize(0);
    }
}