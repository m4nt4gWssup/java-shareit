package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingFullDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingFullDto postRequest(Long userId, BookingDto booking) {
        Item item = itemRepository.getById(booking.getItemId());
        if (item == null) {
            throw new NullPointerException("Такой вещи нет");
        }
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь занята!");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new NullPointerException("Такого пользователя нет!");
        }
        if (itemRepository.getById(booking.getItemId()).getOwner().getId().equals(userId)) {
            throw new NullPointerException("Владелец не может забронировать свою вещь!");
        }
        if (booking.getEnd() == null || booking.getStart() == null
                || booking.getEnd().equals(booking.getStart())
                || booking.getEnd().isBefore(booking.getStart())
                || booking.getStart().isAfter(booking.getEnd())
                || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Неправильно указано время!");
        }
        Booking book = new Booking();
        book.setStart(booking.getStart());
        book.setEnd(booking.getEnd());
        book.setItem(itemRepository.getById(booking.getItemId()));
        book.setBooker(userRepository.getById(userId));
        book.setStatus(Status.WAITING);
        return bookingMapper.toBookingFullDto(repository.save(book));
    }

    @Override
    public BookingFullDto postApproveBooking(Long bookingId, Long userId, boolean approve) {
        if (!repository.getById(bookingId).getItem().getOwner().getId().equals(userId)) {
            throw new NullPointerException("Менять статус бронирования может только владелец");
        }
        if (repository.getReferenceById(bookingId).getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Статус уже подтвержден!");
        }
        Booking book = repository.getById(bookingId);
        if (approve) {
            book.setStatus(Status.APPROVED);
        } else {
            book.setStatus(Status.REJECTED);
        }
        return bookingMapper.toBookingFullDto(repository.save(book));
    }

    @Override
    public BookingFullDto getBookingRequest(Long bookingId, Long userId) {
        Booking book = repository.getById(bookingId);
        if (!book.getBooker().getId().equals(userId)) {
            if (!book.getItem().getOwner().getId().equals(userId)) {
                throw new NullPointerException("Смотреть может владелец или арендующий");
            }
        }
        return bookingMapper.toBookingFullDto(book);
    }

    @Override
    public List<BookingFullDto> getAllBookingRequestForUser(Long userId, String state) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NullPointerException("Такого пользователя нет");
        }
        List<BookingFullDto> bookingsDto = new ArrayList<>();
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Order.desc("start"));
        switch (state) {
            case "CURRENT":
                bookings = repository.getCurrentByUserId(userId);
                bookings.sort(Comparator.comparing(Booking::getStart));
                for (Booking book : bookings) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "PAST":
                bookings = repository.getBookingByUserIdAndFinishAfterNow(userId);
                bookings.sort(Comparator.comparing(Booking::getStart).reversed());
                for (Booking book : bookings) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "FUTURE":
                List<Booking> bookingsApprove = repository.findByBookerAndStatus(userRepository.getById(userId),
                        Status.APPROVED, sort);
                List<Booking> bookingsWaiting = repository.findByBookerAndStatus(userRepository.getById(userId),
                        Status.WAITING, sort);
                for (Booking book : bookingsApprove) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                for (Booking book : bookingsWaiting) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                bookingsDto.sort(Comparator.comparing(BookingFullDto::getStart).reversed());
                return bookingsDto;
            case "WAITING":
                bookings = repository.findByBookerAndStatus(userRepository.getById(userId),
                        Status.WAITING, sort);
                bookings.sort(Comparator.comparing(Booking::getStart).reversed());
                for (Booking book : bookings) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "REJECTED":
                bookings = repository.findByBookerAndStatus(userRepository.getById(userId),
                        Status.REJECTED, sort);
                bookings.sort(Comparator.comparing(Booking::getStart).reversed());
                for (Booking book : bookings) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "ALL":
                bookings = repository.findByBooker(userRepository.getById(userId), sort);
                bookings.sort(Comparator.comparing(Booking::getStart).reversed());
                for (Booking book : bookings) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingFullDto> getAllBookingRequestForOwner(Long userId, String state) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NullPointerException("Такого пользователя нет");
        }
        List<Item> ownerItems = itemRepository.findAllByOwnerOrderById(userRepository.getById(userId));
        List<BookingFullDto> bookingsDto = new ArrayList<>();
        List<Booking> bookings;
        switch (state) {
            case "CURRENT":
                bookings = repository.findAllByItemInAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerItems, LocalDateTime.now(), LocalDateTime.now());
                bookings.sort(Comparator.comparing(Booking::getEnd).reversed());
                for (Booking book : bookings) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "PAST":
                bookings = repository.findAllByItemInAndStatusAndEndBeforeOrderByStartDesc(ownerItems,
                        Status.APPROVED, LocalDateTime.now());
                bookings.sort(Comparator.comparing(Booking::getEnd).reversed());
                for (Booking book : bookings) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "FUTURE":
                List<Booking> bookingsApprove = repository.findByItemInAndStatus(ownerItems,
                        Status.APPROVED);
                List<Booking> bookingsWaiting = repository.findByItemInAndStatus(ownerItems,
                        Status.WAITING);
                for (Booking book : bookingsApprove) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                for (Booking book : bookingsWaiting) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                bookingsDto.sort(Comparator.comparing(BookingFullDto::getStart).reversed());
                return bookingsDto;
            case "WAITING":
                bookings = repository.findByItemInAndStatus(ownerItems,
                        Status.WAITING);
                bookings.sort(Comparator.comparing(Booking::getStart).reversed());
                for (Booking book : bookings) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "REJECTED":
                bookings = repository.findByItemInAndStatus(ownerItems,
                        Status.REJECTED);
                bookings.sort(Comparator.comparing(Booking::getStart).reversed());
                for (Booking book : bookings) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            case "ALL":
                bookings = repository.findByItemIn(ownerItems);
                bookings.sort(Comparator.comparing(Booking::getStart).reversed());
                for (Booking book : bookings) {
                    bookingsDto.add(bookingMapper.toBookingFullDto(book));
                }
                return bookingsDto;
            default:
                throw new ValidationException("Unknown state: " + state);
        }
    }

    @Override
    public Booking getBookingWithUserBookedItem(Long itemId, Long userId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NullPointerException("Такой вещи не существует");
        }
        return repository.findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), Status.APPROVED);
    }
}
