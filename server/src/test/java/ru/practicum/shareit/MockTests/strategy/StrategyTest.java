package ru.practicum.shareit.MockTests.strategy;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.booking.status.Strategy;
import ru.practicum.shareit.booking.status.StrategyFactory;
import ru.practicum.shareit.exception.InvalidParameterForBooking;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ComponentScan(basePackages = "ru.practicum.shareit")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional //
public class StrategyTest {
    @MockBean
    private BookingRepository bookingRepository;
    @Autowired
    private StrategyFactory strategyFactory;

    private Set<Strategy> strategySet;

    @BeforeEach
    public void setUp() {
        strategySet = new HashSet<>();
        Strategy mockStrategy1 = new MockStrategy(Status.FUTURE, bookingRepository);
        Strategy mockStrategy2 = new MockStrategy(Status.PAST, bookingRepository);
        Strategy mockStrategy3 = new MockStrategy(Status.ALL, bookingRepository);
        strategySet.add(mockStrategy1);
        strategySet.add(mockStrategy2);
        strategySet.add(mockStrategy3);


        strategyFactory = new StrategyFactory(strategySet);
    }

    @Test
    public void testFindStrategyValidStatus() {
        Strategy strategy = strategyFactory.findStrategy(Status.FUTURE);
        assertThat(strategy).isNotNull();
        assertThat(strategy.getStatusName()).isEqualTo(Status.FUTURE);
    }

    @Test
    public void testFindStrategyInvalidStatusThrowsException() {
        assertThrows(InvalidParameterForBooking.class, () -> {
            strategyFactory.findStrategy(Status.CANCELED);
        });
    }

    @Test
    public void testSearchBookings() {
        Long userId = 1L;
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        ;

        when(bookingRepository.findByBookerIdOrItemOwnerId(userId, userId))
                .thenReturn(Arrays.asList(booking1, booking2));

        Strategy strategy = strategyFactory.findStrategy(Status.ALL);
        List<Booking> bookings = strategy.searchBookings(userId);

        assertThat(bookings).hasSize(2);
        assertThat(bookings).containsExactlyInAnyOrder(booking1, booking2);
    }

    private static class MockStrategy implements Strategy {
        private final Status statusName;
        private final BookingRepository bookingRepository;

        public MockStrategy(Status statusName, BookingRepository bookingRepository) {
            this.statusName = statusName;
            this.bookingRepository = bookingRepository;
        }

        @Override
        public Status getStatusName() {
            return statusName;
        }

        @Override
        public List<Booking> searchBookings(Long id) {
            return bookingRepository.findByBookerIdOrItemOwnerId(id, id);
        }
    }
}
