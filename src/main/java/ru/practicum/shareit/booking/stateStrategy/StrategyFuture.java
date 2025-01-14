package ru.practicum.shareit.booking.stateStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StrategyFuture implements Strategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> searchBookings(Long id) {
        log.info("Поиск бронирований со статусом FUTURE (будущих) для пользователя" + id);
        return bookingRepository.findByUserIdFutureBook(
                id,
                LocalDateTime.now());
    }

    @Override
    public Status getStatusName() {
        return Status.FUTURE;
    }
}

