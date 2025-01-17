package ru.practicum.shareit.booking.status;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StrategyWaiting implements Strategy {
    private final BookingRepository bookingRepository;

    @Override
    public List<Booking> searchBookings(Long id) {
        log.info("Поиск бронирований со статусом WAITING (ожидающих одобрения) для пользователя{}", id);
        return bookingRepository.findByUserIdAndStatus(
                id,
                Status.WAITING);
    }

    @Override
    public Status getStatusName() {
        return Status.WAITING;
    }
}
