package ru.practicum.shareit.server.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.server.request.ItemRequest;
import ru.practicum.shareit.server.request.ItemRequestMapper;
import ru.practicum.shareit.server.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.server.request.dto.ItemRequestDto;
import ru.practicum.shareit.server.request.repository.ItemRequestRepository;
import ru.practicum.shareit.server.user.User;
import ru.practicum.shareit.server.user.repository.UserRepository;
import ru.practicum.shareit.server.validation.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRequestMapper mapper;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestCreateDto createDto) {
        log.info("Создание запроса на вещь пользователя с id={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));

        ItemRequest request = mapper.toEntity(createDto, user);
        ItemRequest saved = requestRepository.save(request);

        log.info("Запрос на вещь успешно создан с id={}", saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    public List<ItemRequestDto> getMyRequests(Long userId) {
        log.info("Получение всех запросов пользователя с id={}", userId);

        checkUserExists(userId);

        return requestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllOtherRequests(Long userId) {
        log.info("Получение запросов от других пользователей. Текущий пользователь id={}", userId);

        checkUserExists(userId);

        return requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        log.info("Получение запроса id={} для пользователя id={}", requestId, userId);

        checkUserExists(userId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден: " + requestId));

        return mapper.toDto(request);
    }

    private void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: " + userId);
        }
    }
}