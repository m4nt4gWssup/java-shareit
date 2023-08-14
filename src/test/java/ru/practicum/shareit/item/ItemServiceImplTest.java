package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.checker.Checker;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private Checker checker;

    @InjectMocks
    private ItemServiceImpl itemService;

    private ItemDto sampleItemDto;
    private User user1;
    private Item item1;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        item1 = new Item();
        user1 = new User();
        user1.setId(1L);
        user1.setName("Testman");
        user1.setEmail("testman12@test.com");
        sampleItemDto = new ItemDto();
        sampleItemDto.setId(1L);
        sampleItemDto.setName("TestItem");
        sampleItemDto.setDescription("Test Description");
        sampleItemDto.setAvailable(true);
        sampleItemDto.setOwner(user1);
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(user1);
    }

    @Test
    public void testCreateUserNotFound() {
        when(checker.isExistUser(any())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> itemService.create(sampleItemDto, 1L));
    }

    @Test
    public void testCreateItemSuccess() {
        when(checker.isExistUser(any())).thenReturn(true);
        when(itemRepository.save(any(Item.class))).thenReturn(new Item());
        itemService.create(sampleItemDto, 1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    public void testUpdateUserNotFound() {
        when(checker.isExistUser(any())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> itemService.update(sampleItemDto, 1L, 1L));
    }

    @Test
    public void testUpdate_ItemOwnerNotMatched_ThrowsException() {
        Long ownerId = 1L;
        Long itemId = 1L;

        Item item = mock(Item.class);
        User user = mock(User.class);

        when(user.getId()).thenReturn(2L);
        when(item.getOwner()).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(checker.isExistUser(ownerId)).thenReturn(true);

        assertThrows(ItemNotFoundException.class, () -> itemService.update(new ItemDto(), ownerId, itemId));
    }

    @Test
    public void testUpdate_ItemNameIsValid_SetName() {
        Long ownerId = 1L;
        Long itemId = 1L;
        String validName = "Valid Name";

        Item item = mock(Item.class);
        User user = mock(User.class);

        when(user.getId()).thenReturn(ownerId);
        when(item.getOwner()).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(checker.isExistUser(ownerId)).thenReturn(true);
        when(checker.isValidString(validName)).thenReturn(true);

        ItemDto itemDto = new ItemDto();
        itemDto.setName(validName);
        itemService.update(itemDto, ownerId, itemId);

        verify(item).setName(validName);
    }

    @Test
    public void testUpdate_ItemDescriptionIsValid_SetDescription() {
        Long ownerId = 1L;
        Long itemId = 1L;
        String validDescription = "Valid Description";

        Item item = mock(Item.class);
        User user = mock(User.class);

        when(user.getId()).thenReturn(ownerId);
        when(item.getOwner()).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(checker.isExistUser(ownerId)).thenReturn(true);
        when(checker.isValidString(validDescription)).thenReturn(true);
        when(checker.isValidString(null)).thenReturn(false);

        ItemDto itemDto = new ItemDto();
        itemDto.setDescription(validDescription);
        itemService.update(itemDto, ownerId, itemId);

        verify(item).setDescription(validDescription);
    }

    @Test
    public void testUpdate_ItemAvailabilityIsNotNull_SetAvailability() {
        Long ownerId = 1L;
        Long itemId = 1L;
        Boolean availability = true;

        Item item = mock(Item.class);
        User user = mock(User.class);

        when(user.getId()).thenReturn(ownerId);
        when(item.getOwner()).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(checker.isExistUser(ownerId)).thenReturn(true);

        ItemDto itemDto = new ItemDto();
        itemDto.setAvailable(availability);
        itemService.update(itemDto, ownerId, itemId);

        verify(item).setAvailable(availability);
    }


    @Test
    public void testUpdate_ItemDtoIdIsNull_ItemDtoIdIsSet() {
        Long ownerId = 1L;
        Long itemId = 1L;

        Item item = mock(Item.class);
        User user = mock(User.class);

        when(user.getId()).thenReturn(ownerId);
        when(item.getOwner()).thenReturn(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(checker.isExistUser(ownerId)).thenReturn(true);

        ItemDto itemDto = new ItemDto();
        itemService.update(itemDto, ownerId, itemId);

        assertEquals(itemId, itemDto.getId());
    }

    @Test
    public void testUpdateItemNotFound() {
        when(checker.isExistUser(any())).thenReturn(true);
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemService.update(sampleItemDto, 1L, 1L));
    }

    @Test
    public void testUpdateItemSuccess() {
        int from = 0;
        int size = 5;
        Long ownerId = 1L;

        User user = new User();
        user.setId(ownerId);
        Item item = new Item();
        item.setOwner(user);

        when(userRepository.getById(ownerId)).thenReturn(user);
        when(checker.isExistUser(ownerId)).thenReturn(true);

        Page<Item> page = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAllByOwnerOrderById(user, PageRequest.of(from, size))).thenReturn(page);

        List<ItemDto> result = itemService.getItemsByOwner(ownerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getName(), result.get(0).getName());
    }


    @Test
    public void testDeleteUserNotFound() {
        when(checker.isExistUser(any())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> itemService.delete(1L, 1L));
    }

    @Test
    public void testDeleteItemNotFound() {
        when(checker.isExistUser(any())).thenReturn(true);
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.delete(1L, 1L));
    }

    @Test
    public void testGetItemByIdUserNotFound() {
        when(checker.isExistUser(any())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> itemService.getItemById(1L, 1L));
    }

    @Test
    public void testGetItemByIdItemNotFound() {
        when(checker.isExistUser(any())).thenReturn(true);
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(1L, 1L));
    }

    @Test
    public void testGetItemsByOwnerUserNotFound() {
        when(checker.isExistUser(any())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> itemService.getItemsByOwner(1L, 0, 10));
    }

    @Test
    public void testGetItemsByOwnerSuccess() {
        int from = 0;
        int size = 5;
        Long ownerId = 1L;

        User user = new User();
        user.setId(ownerId);
        Item item = new Item();
        item.setOwner(user);

        when(userRepository.getById(ownerId)).thenReturn(user);

        when(checker.isExistUser(ownerId)).thenReturn(true);

        Page<Item> page = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAllByOwnerOrderById(user, PageRequest.of(from, size))).thenReturn(page);

        List<ItemDto> result = itemService.getItemsByOwner(ownerId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getName(), result.get(0).getName());
    }


    @Test
    public void testGetItemsBySearchQuerySuccess() {
        List<Item> items = new ArrayList<>();
        Page<Item> itemPage = new PageImpl<>(items);

        when(itemRepository.getItemsBySearchQuery(any(), any())).thenReturn(itemPage);

        itemService.getItemsBySearchQuery("test", 0, 10);
        verify(itemRepository).getItemsBySearchQuery(any(), any());
    }


    @Test
    public void testCreateCommentUserNotFound() {
        when(checker.isExistUser(any())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> itemService.createComment(new CommentDto(), 1L, 1L));
    }

    @Test
    public void testCreateCommentNoBookingForItem() {
        when(checker.isExistUser(any())).thenReturn(true);
        when(checker.getUserBookingForItem(any(), any())).thenReturn(null);
        assertThrows(ValidationException.class, () -> itemService.createComment(new CommentDto(), 1L, 1L));
    }

    @Test
    public void testFindByIdItemNotFound() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ItemNotFoundException.class, () -> itemService.findById(1L));
    }

    @Test
    public void testFindByIdSuccess() {
        when(itemRepository.findById(any())).thenReturn(Optional.of(new Item()));
        itemService.findById(1L);
        verify(itemRepository).findById(any());
    }

    @Test
    void getItemById_UserNotFound() {
        Long itemId = 1L;
        Long userId = 1L;

        when(checker.isExistUser(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> itemService.getItemById(itemId, userId));
    }
}
