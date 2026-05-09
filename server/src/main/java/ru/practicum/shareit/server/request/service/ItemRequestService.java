package ru.practicum.shareit.server.request.service;

import ru.practicum.shareit.server.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(Long userId, ItemRequestCreateDto createDto);

    List<ItemRequestDto> getMyRequests(Long userId);

    List<ItemRequestDto> getAllOtherRequests(Long userId);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}