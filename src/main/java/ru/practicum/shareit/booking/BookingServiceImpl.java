package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Transactional
    @Override
    public BookingFullDto postRequest(Long userId, BookingDto booking) {
        Item item = itemService.findById(booking.getItemId());
        User booker = userService.findById(userId);
        if (!item.getAvailable()) {
            throw new BookingNotAvailableException("Вещь занята!");
        }
        if (item.getOwner().equals(booker)) {
            throw new BookingByOwnerNotAvailableException("Владелец не может забронировать свою вещь!");
        }
        if (booking.getEnd() == null || booking.getStart() == null
                || booking.getEnd().equals(booking.getStart())
                || booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().isAfter(booking.getEnd())
                || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Неправильно указано время!");
        }
        Booking book = new Booking();
        if (book.getId() == null) {
            book.setId(booking.getId());
        }
        book.setStart(booking.getStart());
        book.setEnd(booking.getEnd());
        book.setItem(itemRepository.getById(booking.getItemId()));
        book.setBooker(userRepository.getById(userId));
        book.setStatus(Status.WAITING);
        log.info("Бронирование успешно создано.");
        return BookingMapper.toBookingFullDto(repository.save(book));
    }

    @Transactional
    @Override
    public BookingFullDto postApproveBooking(Long bookingId, Long userId, boolean approve) {
        if (!repository.getById(bookingId).getItem().getOwner().getId().equals(userId)) {
            throw new UpdateNotAvailableException("Менять статус бронирования может только владелец");
        }
        if (repository.getById(bookingId).getStatus().equals(Status.APPROVED)) {
            throw new StatusChangingNotAvailableException("Статус уже подтвержден!");
        }
        Booking book = repository.getById(bookingId);
        if (approve) {
            book.setStatus(Status.APPROVED);
        } else {
            book.setStatus(Status.REJECTED);
        }
        log.info("Статус бронирования успешно изменен.");
        return BookingMapper.toBookingFullDto(book);
    }

    @Transactional
    @Override
    public BookingFullDto getBookingRequest(Long bookingId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID: {} не найден", userId);
                    return new UserNotFoundException("Такого пользователя нет");
                });
        Booking booking = findById(bookingId);
        if (!booking.getBooker().getId().equals(userId)) {
            if (!booking.getItem().getOwner().getId().equals(userId)) {
                throw new GettingNotAvailableException(
                        String.format("Пользователь с ID=%d не является владельцем или бронировавшим товар!", userId));
            }
        }
        return BookingMapper.toBookingFullDto(booking);
    }

    @Transactional
    @Override
    public List<BookingFullDto> getAllBookingRequestForUser(Long userId, String state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID: {} не найден", userId);
                    return new UserNotFoundException("Такого пользователя нет");
                });
        if (from < 0 || size < 0 || size == 0) {
            throw new ValidationException("Неправильно указаны размеры");
        }
        List<BookingFullDto> bookingsDto = new ArrayList<>();
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = repository.getCurrentByUserId(userId,
                        getPage(from, size, Sort.by(Sort.Direction.ASC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "PAST":
                bookings = repository.getBookingByUserIdAndFinishAfterNow(userId,
                        getPage(from, size,
                                Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "FUTURE":
                List<Booking> bookingsApprove = repository.findByBookerAndStatus(userRepository.getById(userId),
                        Status.APPROVED, getPage(from, size)).getContent();
                List<Booking> bookingsWaiting = repository.findByBookerAndStatus(userRepository.getById(userId),
                        Status.WAITING, getPage(from, size)).getContent();
                for (Booking book : bookingsApprove) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                for (Booking book : bookingsWaiting) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                bookingsDto.sort(Comparator.comparing(BookingFullDto::getStart).reversed());
                return bookingsDto;
            case "WAITING":
                bookings = repository.findByBookerAndStatus(userRepository.getById(userId),
                        Status.WAITING, getPage(from, size,
                                Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "REJECTED":
                bookings = repository.findByBookerAndStatus(userRepository.getById(userId),
                        Status.REJECTED, getPage(from, size, Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "ALL":
                bookings = repository.findByBooker(userRepository.getById(userId),
                        getPage(from, size, Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    @Transactional
    @Override
    public List<BookingFullDto> getAllBookingRequestForOwner(Long userId, String state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Пользователь с ID: {} не найден", userId);
                    return new UserNotFoundException("Такого пользователя нет");
                });
        if (from < 0 || size < 0 || size == 0) {
            throw new ValidationException("Неправильно указаны размеры!");
        }
        List<Item> ownerItems = itemRepository.findAllByOwnerOrderById(userRepository.getById(userId),
                getPage(0, 10)).toList();
        List<BookingFullDto> bookingsDto = new ArrayList<>();
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = repository.findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerItems, LocalDateTime.now(), LocalDateTime.now(), getPage(from, size,
                                Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "PAST":
                bookings = repository.findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(ownerItems,
                        Status.APPROVED, LocalDateTime.now(), getPage(from, size,
                                Sort.by(Sort.Direction.ASC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "FUTURE":
                List<Booking> bookingsApprove = repository.findByItemInAndStatus(ownerItems,
                        Status.APPROVED, getPage(from, size)).getContent();
                List<Booking> bookingsWaiting = repository.findByItemInAndStatus(ownerItems,
                        Status.WAITING, getPage(from, size)).getContent();
                for (Booking book : bookingsApprove) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                for (Booking book : bookingsWaiting) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                bookingsDto.sort(Comparator.comparing(BookingFullDto::getStart).reversed());
                return bookingsDto;
            case "WAITING":
                bookings = repository.findByItemInAndStatus(ownerItems,
                        Status.WAITING, getPage(from, size,
                                Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "REJECTED":
                bookings = repository.findByItemInAndStatus(ownerItems,
                        Status.REJECTED, getPage(from, size,
                                Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "ALL":
                bookings = repository.findByItemIn(ownerItems, PageRequest.of(from / size, size,
                        Sort.by(Sort.Direction.DESC, "start"))).getContent();
                for (Booking book : bookings) {
                    bookingsDto.add(BookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    @Transactional
    @Override
    public Booking getBookingWithUserBookedItem(Long itemId, Long userId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            log.error("Вещь с ID: {} не найдена", itemId);
            throw new ItemNotFoundException("Такой вещи не существует");
        }
        return repository.findFirstByItemIdAndBookerIdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), Status.APPROVED);
    }

    private Booking findById(Long bookingId) {
        return repository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("Бронирование с ID={} не найдено", bookingId);
                    return new BookingNotFoundException(String.format("Бронирование с ID=%d не найдено", bookingId));
                });
    }

    private PageRequest getPage(int from, int size, Sort sort) {
        return PageRequest.of(from / size, size, sort);
    }

    private PageRequest getPage(int from, int size) {
        return PageRequest.of(from / size, size);
    }
}
