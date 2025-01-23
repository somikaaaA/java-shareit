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
import ru.practicum.shareit.booking.stateStrategy.Status;
import ru.practicum.shareit.booking.stateStrategy.StrategyCurrent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor
@Transactional
public class StrategyCurrentTest {
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private StrategyCurrent strategyCurrent;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchBookingsReturnsEmptyListWhenNoBookings() {
        Long userId = 1L;
        when(bookingRepository.findByUserIdCurrentBook(eq(userId), any(LocalDateTime.class)))
                .thenReturn(List.of());
        List<Booking> bookings = strategyCurrent.searchBookings(userId);
        assertThat(bookings).isEmpty();
    }

    @Test
    public void testGetStatusName() {
        assertThat(strategyCurrent.getStatusName()).isEqualTo(Status.CURRENT);
    }
}
