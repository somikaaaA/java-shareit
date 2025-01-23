package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.stateStrategy.Status;
import ru.practicum.shareit.booking.stateStrategy.Strategy;
import ru.practicum.shareit.booking.stateStrategy.StrategyFactory;
import ru.practicum.shareit.exception.BookingIdNotFoundException;
import ru.practicum.shareit.exception.InvalidParameterForBooking;
import ru.practicum.shareit.exception.ItemIdNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userservice;
    private final ItemRepository itemRepository;
    private final StrategyFactory strategyFactory;

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new InvalidParameterForBooking("Дата начала и конца бронирования не может совпадать");
        }
        User user = userservice.getUserById(userId);

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemIdNotFoundException("Вещь с id " + bookingDto.getItemId() + " не найдена"));
        if (!item.getAvailable()) {
            throw new InvalidParameterForBooking("Вещь не доступна для бронирования");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        BookingDto savedBookingDto = BookingMapper.toBookingDto(bookingRepository.save(booking));
        log.info("Бронирование {} сохранено в базу данных", booking);
        return savedBookingDto;
    }

    @Override
    @Transactional
    public BookingDto createApprove(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingIdNotFoundException("Вещь с id " + bookingId + " не найдена"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new InvalidParameterForBooking("Статус может менять только хозяин вещи. Пользователь с id " +
                    userId + " не является хозяином вещи с id " + booking.getItem().getId());
        }
        User user = userservice.getUserById(userId);
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        log.info("Сохранение " + booking + " в базу данных");
        return BookingMapper.toBookingDto(bookingRepository.save(booking));

    }

    @Override
    public List<BookingDto> searchBookingsForOwner(Long ownerId) {
        return toListBookingDto(bookingRepository.findByItemOwnerId(ownerId));
    }

    @Override
    public BookingDto searchBooking(Long userId, Long bookingId) {
        return BookingMapper.toBookingDto(bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() ->
                        new BookingIdNotFoundException("Бронирование для пользователя с id " + userId + " не найдено")));
    }

    @Override
    public List<BookingDto> searchBookingsWithState(Long userId, Status state) {
        Strategy strategy = strategyFactory.findStrategy(state);
        return toListBookingDto(strategy.searchBookings(userId));
    }

    @Override
    public Booking lastBookingForItem(Long id) { //текущее бронирование
        return bookingRepository.findByItemIdCurrentBook(id, LocalDateTime.now())
                .orElse(null);
    }

    @Override
    public Booking nextBookingForItem(Long id) { //следующее бронирование
        return bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(id, LocalDateTime.now())
                .orElse(null);
    }

    private List<BookingDto> toListBookingDto(List<Booking> list) {
        return list.stream()
                .map(BookingMapper::toBookingDto)
                .toList();
    }
}
