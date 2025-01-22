package ru.practicum.shareit.MockTests.strategy;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.booking.status.StrategyFuture;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor
@Transactional
public class StrategyFutureTest {
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private StrategyFuture strategyFuture;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchBookingsReturnsEmptyListWhenNoFutureBookings() {
        Long userId = 1L;
        when(bookingRepository.findByUserIdFutureBook(userId, LocalDateTime.now()))
                .thenReturn(Arrays.asList());
        List<Booking> bookings = strategyFuture.searchBookings(userId);
        assertThat(bookings).isEmpty();
    }

    @Test
    public void testGetStatusName() {
        assertThat(strategyFuture.getStatusName()).isEqualTo(Status.FUTURE);
    }
}
