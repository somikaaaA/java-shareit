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
import ru.practicum.shareit.booking.stateStrategy.StrategyAll;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor
@Transactional
public class StrategyAllTest {
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private StrategyAll strategyAll;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSearchBookingsReturnsEmptyListWhenNoBookings() {
        Long userId = 1L;
        when(bookingRepository.findByBookerIdOrItemOwnerId(userId, userId))
                .thenReturn(List.of());
        List<Booking> bookings = strategyAll.searchBookings(userId);
        assertThat(bookings).isEmpty();
    }

    @Test
    public void testGetStatusName() {
        assertThat(strategyAll.getStatusName()).isEqualTo(Status.ALL);
    }
}

