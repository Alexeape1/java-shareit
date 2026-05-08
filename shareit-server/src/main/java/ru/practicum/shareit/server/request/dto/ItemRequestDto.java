package ru.practicum.shareit.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.server.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private List<ItemForRequestDto> items;
}
