package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    private MockMvc mvc;
    private final ObjectMapper mapper = new ObjectMapper();

    Item item1 = new Item();
    Item item2 = new Item();
    User user1 = new User();
    User user2 = new User();
    Comment comment = new Comment();
    Booking booking1 = new Booking();


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setAvailable(false);
        item2.setOwner(user2);
        user1.setId(1L);
        user1.setName("Testman");
        user1.setEmail("testman12@test.com");
        user2.setId(2L);
        user2.setName("Testman2");
        user2.setEmail("testman123@test.com");
        comment.setId(1L);
        comment.setText("text");
        comment.setCreated(LocalDateTime.now().plusMinutes(1));
        comment.setAuthor(user1);
        booking1.setId(1L);
        booking1.setStart(LocalDateTime.now().plusDays(1));
        booking1.setEnd(LocalDateTime.now().plusDays(2));
        booking1.setItem(item1);
        booking1.setBooker(user1);
        booking1.setStatus(Status.WAITING);
    }

    @Test
    void postRequest_ItemNotAvailable_ThrowsException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);

        when(itemService.findById(1L)).thenReturn(item1);
        when(userService.findById(anyLong())).thenReturn(user2);

        assertThrows(ValidationException.class, () -> bookingService.postRequest(1L, bookingDto));
    }

    @Test
    void postApproveBooking_UserNotOwner_ThrowsException() {
        Booking booking = new Booking();
        booking.setItem(item1);

        when(repository.getById(anyLong())).thenReturn(booking);

        assertThrows(UpdateNotAvailableException.class, () -> bookingService.postApproveBooking(1L, 2L, true));
    }

    @Test
    void postRequest_BookingByOwner_ThrowsException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        User user = new User();

        Item item = new Item();
        item.setAvailable(true);
        item.setOwner(user);

        when(itemService.findById(anyLong())).thenReturn(item);
        when(userService.findById(anyLong())).thenReturn(user);

        assertThrows(BookingByOwnerNotAvailableException.class, () -> bookingService.postRequest(1L, bookingDto));
    }

    @Test
    void postRequest_InvalidBookingTimes_ThrowsException() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        bookingDto.setEnd(LocalDateTime.now().minusDays(2));

        when(itemService.findById(1L)).thenReturn(item1);
        when(userService.findById(anyLong())).thenReturn(user2);

        assertThrows(ValidationException.class, () -> bookingService.postRequest(1L, bookingDto));
    }

    @Test
    void getBookingRequest_UserNotBookerOrOwner_ThrowsException() {
        Booking booking = new Booking();
        booking.setBooker(user2);
        booking.setItem(item1);

        when(repository.findById(anyLong())).thenReturn(java.util.Optional.of(booking));

        assertThrows(GettingNotAvailableException.class, () -> bookingService.getBookingRequest(1L, 3L));
    }

    @Test
    void getAllBookingRequestForUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.getAllBookingRequestForUser(1L, "CURRENT", 1, 10));
    }

    @Test
    void getAllBookingRequestForOwner_UserNotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.getAllBookingRequestForOwner(1L, "CURRENT", 1, 10));
    }

    @Test
    void getBookingWithUserBookedItem_ItemNotFound_ThrowsException() {
        when(itemRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> bookingService.getBookingWithUserBookedItem(1L, 2L));
    }

    @Test
    public void testPostRequest() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(booking1.getStart());
        bookingDto.setEnd(booking1.getEnd());

        Mockito.when(itemService.findById(1L)).thenReturn(item1);
        Mockito.when(userService.findById(2L)).thenReturn(user2);
        Mockito.when(itemRepository.getById(1L)).thenReturn(item1);
        Mockito.when(userRepository.getById(2L)).thenReturn(user2);
        Mockito.when(repository.save(any(Booking.class))).thenReturn(booking1);

        BookingFullDto result = bookingService.postRequest(2L, bookingDto);

        assertNotNull(result);
        assertEquals(1, result.getItem().getId().longValue());
        assertEquals(1, result.getBooker().getId().longValue());
        assertEquals(Status.WAITING, result.getStatus());
    }

    @Test
    public void testPostApproveBooking() {
        Mockito.when(repository.save(booking1)).thenReturn(booking1);
        repository.save(booking1);
        Mockito.when(repository.getById(1L)).thenReturn(booking1);

        BookingFullDto result = bookingService.postApproveBooking(1L, 1L, true);
        assertNotNull(result);
        assertEquals(Status.APPROVED, result.getStatus());
    }

    @Test
    public void testGetBookingRequest() {
        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(booking1));
        BookingFullDto result = bookingService.getBookingRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(1, result.getBooker().getId());
    }

    @Test
    void testGetAllBookingRequestForUser() {
        String state = "CURRENT";
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), item2,
                user2, Status.WAITING);
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(repository.getCurrentByUserId(Mockito.anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> expectedResults = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        List<BookingFullDto> actualResults = bookingService.getAllBookingRequestForUser(1L, state, 1, 10);
        assertEquals(expectedResults.size(), actualResults.size());
    }

    @Test
    void testGetAllBookingRequestForOwner() {
        String state = "CURRENT";
        List<Item> ownerItems = Arrays.asList(item1, item2);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), item2,
                user2, Status.WAITING);
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        Page pageList = new PageImpl(ownerItems, PageRequest.of(1, 10), 2);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(itemRepository.findAllByOwnerOrderById(any(), any()))
                .thenReturn(pageList);
        Mockito.when(repository.findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
                        any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> expectedResults = bookings.stream()
                .map(BookingMapper::toBookingFullDto)
                .collect(Collectors.toList());

        List<BookingFullDto> actualResults = bookingService.getAllBookingRequestForOwner(1L, state, 1, 10);
        assertEquals(expectedResults.size(), actualResults.size());
    }

    @Test
    void testGetAllBookingRequestForUserFuture() {
        String state = "FUTURE";
        List<Booking> bookingsApprove = new ArrayList<>();
        List<Booking> bookingsWaiting = new ArrayList<>();
        bookingsWaiting.add(booking1);
        bookingsApprove.add(new Booking(2L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3),
                item2, user1, Status.APPROVED));

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1L)).thenReturn(user1);
        Mockito.when(repository.findByBookerAndStatus(any(User.class), Mockito.eq(Status.APPROVED),
                any(Pageable.class))).thenReturn(new PageImpl<>(bookingsApprove));
        Mockito.when(repository.findByBookerAndStatus(any(User.class), Mockito.eq(Status.WAITING),
                any(Pageable.class))).thenReturn(new PageImpl<>(bookingsWaiting));

        List<BookingFullDto> result = bookingService.getAllBookingRequestForUser(1L, state, 0, 10);

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllBookingRequestForOwnerFuture() {
        String state = "FUTURE";
        List<Item> ownerItems = new ArrayList<>();
        ownerItems.add(item1);
        ownerItems.add(new Item(3L, "Item 3", "Description 3", true, user1, null));

        List<Booking> bookingsApprove = new ArrayList<>();
        List<Booking> bookingsWaiting = new ArrayList<>();
        bookingsApprove.add(new Booking(2L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3),
                ownerItems.get(1), user1, Status.APPROVED));
        bookingsWaiting.add(booking1);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1L)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(user1,
                PageRequest.of(0, 10))).thenReturn(new PageImpl<>(ownerItems));
        Mockito.when(repository.findByItemInAndStatus(ownerItems, Status.APPROVED,
                PageRequest.of(0 / 10, 10))).thenReturn(new PageImpl<>(bookingsApprove));
        Mockito.when(repository.findByItemInAndStatus(ownerItems, Status.WAITING,
                PageRequest.of(0 / 10, 10))).thenReturn(new PageImpl<>(bookingsWaiting));

        List<BookingFullDto> result = bookingService.getAllBookingRequestForOwner(1L, state, 0, 10);

        assertEquals(2, result.size());
    }

    @Test
    void testGetAllBookingRequestForUserAll() {
        String state = "ALL";
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(3));
        booking2.setBooker(user2);
        booking2.setStatus(Status.WAITING);
        booking2.setItem(item2);
        List<Booking> allBookings = new ArrayList<>();
        allBookings.add(booking1);
        allBookings.add(booking2);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1L)).thenReturn(user1);
        Mockito.when(repository.findByBooker(userRepository.getById(1L), PageRequest.of(0 / 10, 10,
                Sort.by(Sort.Direction.DESC, "start")))).thenReturn(new PageImpl<>(allBookings));

        List<BookingFullDto> result = bookingService.getAllBookingRequestForUser(1L, state, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(booking1.getId(), result.get(0).getId());
        assertEquals(booking1.getStatus(), result.get(0).getStatus());
        assertEquals(booking2.getId(), result.get(1).getId());
        assertEquals(booking2.getStatus(), result.get(1).getStatus());
    }

    @Test
    void testGetAllBookingRequestForOwnerAll() {
        String state = "ALL";
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(3));
        booking2.setBooker(user2);
        booking2.setItem(item2);
        booking2.setStatus(Status.WAITING);
        List<Booking> allBookings = new ArrayList<>();
        allBookings.add(booking1);
        allBookings.add(booking2);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1L)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(userRepository.getById(1L),
                PageRequest.of(0, 10))).thenReturn(new PageImpl<>(Arrays.asList(item1, item2)));
        List<Item> ownerItems = itemRepository.findAllByOwnerOrderById(userRepository.getById(1L),
                PageRequest.of(0, 10)).toList();
        Mockito.when(repository.findByItemIn(ownerItems, PageRequest.of(0 / 10, 10,
                Sort.by(Sort.Direction.DESC, "start")))).thenReturn(new PageImpl<>(allBookings));

        List<BookingFullDto> result = bookingService.getAllBookingRequestForOwner(1L, state, 0, 10);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(booking1.getId(), result.get(0).getId());
        assertEquals(booking1.getStatus(), result.get(0).getStatus());
        assertEquals(booking2.getId(), result.get(1).getId());
        assertEquals(booking2.getStatus(), result.get(1).getStatus());
    }

    @Test
    void testGetAllBookingRequestForOwnerStateWaiting() {
        String state = "WAITING";
        List<Item> ownerItems = Arrays.asList(item1, item2);

        booking1.setStatus(Status.WAITING);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.now().plusHours(2));
        booking2.setItem(item2);
        booking2.setBooker(user1);
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1L)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(any(), any()))
                .thenReturn(new PageImpl<>(ownerItems));
        Mockito.when(repository.findByItemInAndStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = bookingService.getAllBookingRequestForOwner(1L, state, 0, 10);
        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }

    @Test
    void testGetAllBookingRequestForOwnerStateRejected() {
        String state = "REJECTED";
        List<Item> ownerItems = Arrays.asList(item1, item2);

        booking1.setStatus(Status.REJECTED);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(Status.REJECTED);
        booking2.setStart(LocalDateTime.now().minusHours(2));
        booking2.setItem(item2);
        booking2.setBooker(user1);
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1L)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(any(), any()))
                .thenReturn(new PageImpl<>(ownerItems));
        Mockito.when(repository.findByItemInAndStatus(any(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = bookingService.getAllBookingRequestForOwner(1L, state, 0, 10);
        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }

    @Test
    void testGetAllBookingRequestForOwnerStatePast() {
        String state = "PAST";
        List<Item> ownerItems = Arrays.asList(item1, item2);

        booking1.setStatus(Status.APPROVED);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().plusHours(2));
        booking2.setItem(item2);
        booking2.setBooker(user1);
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1L)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(any(), any()))
                .thenReturn(new PageImpl<>(ownerItems));
        Mockito.when(repository.findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(
                        any(), Mockito.eq(Status.APPROVED), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = bookingService.getAllBookingRequestForOwner(1L, state, 0, 10);
        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }

    @Test
    public void postApproveBooking_ThrowsStatusChangingNotAvailableException() {
        Booking booking = new Booking();
        booking.setItem(new Item());

        User owner = new User();
        owner.setId(2L);
        booking.getItem().setOwner(owner);

        booking.setStatus(Status.APPROVED);

        User booker = new User();
        booker.setId(1L);
        booking.setBooker(booker);

        when(repository.getById(1L)).thenReturn(booking);

        assertThrows(StatusChangingNotAvailableException.class, () -> {
            bookingService.postApproveBooking(1L, 2L, true);
        });
    }

    @Test
    public void getAllBookingRequestForUser_ThrowsUnsupportedStatusException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        assertThrows(UnsupportedStatusException.class, () -> {
            bookingService.getAllBookingRequestForUser(1L, "UNKNOWN_STATE", 0, 10);
        });
    }

    @Test
    public void testPostRequest_ItemNotAvailable() {
        Item item = new Item();
        item.setAvailable(false);
        when(itemService.findById(123L)).thenReturn(item);

        User mockUser = new User();
        mockUser.setId(1L);
        when(userService.findById(anyLong())).thenReturn(mockUser);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(123L);

        assertThrows(BookingNotAvailableException.class, () -> bookingService.postRequest(1L, bookingDto));
    }

    @Test
    public void testPostApproveBooking_Rejected() {
        Item mockItem = new Item();
        User mockOwner = new User();
        mockItem.setOwner(mockOwner);
        mockOwner.setId(1L);

        User mockBooker = new User();
        mockBooker.setId(2L);

        Booking booking = new Booking();
        booking.setItem(mockItem);
        booking.setBooker(mockBooker);
        booking.setStatus(Status.WAITING);

        when(repository.getById(anyLong())).thenReturn(booking);

        bookingService.postApproveBooking(1L, 1L, false);
        assertEquals(Status.REJECTED, booking.getStatus());
    }

    @Test
    public void testGetAllBookingRequestForUser_InvalidSizes() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        assertAll(
                () -> assertThrows(ValidationException.class, () -> bookingService.getAllBookingRequestForUser(1L, "ANY", -1, 10)),
                () -> assertThrows(ValidationException.class, () -> bookingService.getAllBookingRequestForUser(1L, "ANY", 10, 0)),
                () -> assertThrows(ValidationException.class, () -> bookingService.getAllBookingRequestForUser(1L, "ANY", 10, -1))
        );
    }

    @Test
    public void testGetAllBookingRequestForOwner_InvalidSizes() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        assertAll(
                () -> assertThrows(ValidationException.class, () -> bookingService.getAllBookingRequestForOwner(1L, "ANY", -1, 10)),
                () -> assertThrows(ValidationException.class, () -> bookingService.getAllBookingRequestForOwner(1L, "ANY", 10, 0)),
                () -> assertThrows(ValidationException.class, () -> bookingService.getAllBookingRequestForOwner(1L, "ANY", 10, -1))
        );
    }

    @Test
    public void testGetAllBookingRequestForOwner_UnknownState() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));

        when(itemRepository.findAllByOwnerOrderById(any(), any())).thenReturn(Page.empty());

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getAllBookingRequestForOwner(1L, "UNKNOWN_STATE", 0, 10));
    }

    @Test
    public void testGetBookingWithUserBookedItem_Valid() {
        Long itemId = 1L;
        Long userId = 1L;

        Item mockItem = new Item();

        Booking mockBooking = new Booking();
        mockBooking.setItem(mockItem);

        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.of(mockItem));
        when(repository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(eq(itemId), eq(userId), any(), eq(Status.APPROVED)))
                .thenReturn(mockBooking);

        Booking result = bookingService.getBookingWithUserBookedItem(itemId, userId);

        assertNotNull(result);
        assertEquals(mockBooking, result);
    }

    @Test
    public void testGetBookingWithUserBookedItem_ItemNotFound() {
        Long itemId = 1L;
        Long userId = 1L;

        when(itemRepository.findById(eq(itemId))).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> bookingService.getBookingWithUserBookedItem(itemId, userId));
    }

    @Test
    void testGetAllBookingRequestForUserStateWaiting() {
        String state = "WAITING";
        List<Item> ownerItems = Arrays.asList(item1, item2);

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(Status.WAITING);
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setItem(item1);
        booking1.setBooker(user1);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(Status.WAITING);
        booking2.setStart(LocalDateTime.now().plusHours(2));
        booking2.setItem(item2);
        booking2.setBooker(user1);

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1L)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(any(), any()))
                .thenReturn(new PageImpl<>(ownerItems));
        Mockito.when(repository.findByBookerAndStatus(
                        Mockito.eq(user1), Mockito.eq(Status.WAITING), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = bookingService.getAllBookingRequestForUser(1L, state, 0, 10);
        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }


    @Test
    void testGetAllBookingRequestForUserStateRejected() {
        String state = "REJECTED";
        List<Item> ownerItems = Arrays.asList(item1, item2);

        Booking booking1 = new Booking();
        booking1.setId(1L);
        booking1.setStatus(Status.REJECTED);
        booking1.setStart(LocalDateTime.now().minusHours(2));
        booking1.setItem(item1);
        booking1.setBooker(user1);

        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(Status.REJECTED);
        booking2.setStart(LocalDateTime.now().minusHours(1));
        booking2.setItem(item2);
        booking2.setBooker(user1);

        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1L)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(any(), any()))
                .thenReturn(new PageImpl<>(ownerItems));
        Mockito.when(repository.findByBookerAndStatus(
                        Mockito.eq(user1), Mockito.eq(Status.REJECTED), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = bookingService.getAllBookingRequestForUser(1L, state, 0, 10);
        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }

    @Test
    void testGetAllBookingRequestForUserStatePast() {
        String state = "PAST";
        List<Item> ownerItems = Arrays.asList(item1, item2);

        booking1.setStatus(Status.APPROVED);
        Booking booking2 = new Booking();
        booking2.setId(2L);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().plusHours(2));
        booking2.setItem(item2);
        booking2.setBooker(user1);
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        List<BookingFullDto> expectedBookingsDto = new ArrayList<>();
        for (Booking book : bookings) {
            expectedBookingsDto.add(BookingMapper.toBookingFullDto(book));
        }

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Mockito.when(userRepository.getById(1L)).thenReturn(user1);
        Mockito.when(itemRepository.findAllByOwnerOrderById(any(), any()))
                .thenReturn(new PageImpl<>(ownerItems));
        Mockito.when(repository.getBookingByUserIdAndFinishAfterNow(
                        Mockito.eq(1L), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingFullDto> result = bookingService.getAllBookingRequestForUser(1L, state, 0, 10);
        assertEquals(expectedBookingsDto.size(), result.size());
        assertEquals(expectedBookingsDto, result);
    }

    @Test
    public void testFindById_BookingNotFound() {
        Long mockBookingId = 1L;

        when(repository.findById(mockBookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.findById(123L));
    }
}
