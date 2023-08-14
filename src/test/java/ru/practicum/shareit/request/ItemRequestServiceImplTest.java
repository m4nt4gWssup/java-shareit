package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequest request;
    private ItemRequestDto requestDto;

    @BeforeEach
    public void setUp() {
        request = new ItemRequest();
        request.setId(1L);
        request.setRequestorId(1L);
        request.setDescription("Test description");
        request.setCreated(LocalDateTime.now());

        requestDto = new ItemRequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("Test description");
    }

    @Test
    public void testCreate() {
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto result = itemRequestService.create(requestDto, 1L);

        assertNotNull(result);
        assertEquals(requestDto.getDescription(), result.getDescription());
    }

    @Test
    public void testCreateWithEmptyDescription() {
        requestDto.setDescription("");

        assertThrows(ValidationException.class, () -> itemRequestService.create(requestDto, 1L));
    }

    @Test
    public void testGetItemRequestById() {
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findByRequestId(1L)).thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.getItemRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals(requestDto.getDescription(), result.getDescription());
    }

    @Test
    public void testGetItemRequestByIdNotFound() {
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getItemRequestById(1L, 1L));
    }

    @Test
    public void testGetAllRequestWithValidationError() {
        assertThrows(ValidationException.class, () -> itemRequestService.getAllRequest(1L, 5, 0));
    }

    @Test
    public void testGetRequest() {
        when(itemRequestRepository.findByRequestorId(1L)).thenReturn(Collections.singletonList(request));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> itemRequestService.getRequest(1L));
    }
}
