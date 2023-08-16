package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.pagination.CustomPageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Transactional
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long requestorId) {
        userService.findById(requestorId);
        log.info("Создание нового запроса от пользователя с ID={}", requestorId);
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isEmpty()) {
            log.error("Ошибка валидации: описание не может быть пустым");
            throw new ValidationException("Описание не может быть пустым");
        }
        ItemRequest newRequest = new ItemRequest();
        newRequest.setRequestorId(requestorId);
        newRequest.setDescription(itemRequestDto.getDescription());
        newRequest.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toRequestDto(itemRequestRepository.save(newRequest));
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        userService.findById(userId);
        log.info("Получение запроса с ID={} для пользователя с ID={}", requestId, userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.error("Запрос с ID={} не найден", requestId);
                    return new ItemRequestNotFoundException(String.format("Запрос с ID=%d не найден", requestId));
                });
        itemRequest.setItems(itemRepository.findByRequestId(requestId));
        return ItemRequestMapper.toRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequest(Long userId, Integer from, Integer size) {
        userService.findById(userId);
        log.info("Получение всех запросов с пагинацией from={}, size={} для пользователя с ID={}", from, size, userId);
        if (from < 0 || size < 0 || from > size || size == 0) {
            log.error("Ошибка валидации: неправильно указаны размеры from={} и size={}", from, size);
            throw new ValidationException("Неправильно указаны размеры");
        }
        List<ItemRequestDto> requests = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.ASC, "created");
        for (ItemRequest request : itemRequestRepository.findAll(CustomPageRequest.of(from, size, sort)).toList()) {
            if (userId.equals(request.getRequestorId())) {
                continue;
            }
            request.setItems(itemRepository.findByRequestId(request.getId()));
            requests.add(ItemRequestMapper.toRequestDto(request));
        }
        return requests;
    }


    @Override
    public List<ItemRequestDto> getRequest(Long userId) {
        userService.findById(userId);
        log.info("Получение всех запросов для пользователя с ID={}", userId);
        List<ItemRequestDto> requests = new ArrayList<>();
        for (ItemRequest request : itemRequestRepository.findByRequestorId(userId)) {
            request.setItems(itemRepository.findByRequestId(request.getId()));
            requests.add(ItemRequestMapper.toRequestDto(request));
        }
        return requests;
    }
}
