package ru.practicum.shareit.MockTests.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.booking.status.Strategy;
import ru.practicum.shareit.booking.status.StrategyFactory;
import ru.practicum.shareit.exception.BookingIdNotFoundException;
import ru.practicum.shareit.exception.InvalidParameterForBooking;
import ru.practicum.shareit.exception.ItemIdNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private StrategyFactory strategyFactory;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("User  1")
                .email("user1@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Item 1")
                .description("Description for Item 1")
                .available(true)
                .owner(user)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        booking = BookingMapper.toBooking(bookingDto, user, item);
    }

    @Test
    void createBooking() {
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDto savedBookingDto = bookingService.createBooking(user.getId(), bookingDto);
        assertNull(savedBookingDto.getItemId());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBookingWhenStartAndEndAreEqual() {
        bookingDto.setEnd(bookingDto.getStart());
        assertThrows(InvalidParameterForBooking.class, () -> bookingService.createBooking(user.getId(), bookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingWhenItemNotFound() {
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.empty());

        assertThrows(ItemIdNotFoundException.class, () -> bookingService.createBooking(user.getId(), bookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingWhenItemNotAvailable() {
        item.setAvailable(false);
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(InvalidParameterForBooking.class, () -> bookingService.createBooking(user.getId(), bookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createApprove() {
        booking.setStatus(Status.WAITING);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto approvedBookingDto = bookingService.createApprove(user.getId(), booking.getId(), true);

        assertEquals(Status.APPROVED, approvedBookingDto.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createApproveWhenBookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingIdNotFoundException.class, () -> bookingService.createApprove(user.getId(), 1L, true));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createApproveWhenUserIsNotOwner() {
        User anotherUser = User.builder().id(2L).name("User2").email("user2@mail.ru").build();
        booking.setItem(Item.builder().owner(anotherUser).build());

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(InvalidParameterForBooking.class, () -> bookingService.createApprove(user.getId(), booking.getId(), true));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void searchBookingsForOwner() {
        when(bookingRepository.findByItemOwnerId(user.getId())).thenReturn(List.of(booking));

        List<BookingDto> bookings = bookingService.searchBookingsForOwner(user.getId());

        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        verify(bookingRepository).findByItemOwnerId(user.getId());
    }

    @Test
    void searchBooking() {
        when(bookingRepository.findByIdAndUserId(booking.getId(), user.getId())).thenReturn(Optional.of(booking));
        BookingDto foundBookingDto = bookingService.searchBooking(user.getId(), booking.getId());
        assertEquals(booking.getId(), foundBookingDto.getId());
        verify(bookingRepository).findByIdAndUserId(booking.getId(), user.getId());
    }

    @Test
    void searchBookingsWithState() {
        Status status = Status.APPROVED;
        Strategy mockStrategy = mock(Strategy.class);
        when(strategyFactory.findStrategy(status)).thenReturn(mockStrategy);
        when(mockStrategy.searchBookings(user.getId())).thenReturn(List.of(booking));
        List<BookingDto> bookings = bookingService.searchBookingsWithState(user.getId(), status);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        verify(strategyFactory).findStrategy(status);
    }

    @Test
    void searchBookingWhenBookingNotFound() {
        when(bookingRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingIdNotFoundException.class, () -> bookingService.searchBooking(user.getId(), booking.getId()));
    }

    @Test
    void lastBookingForItem() {
        when(bookingRepository.findByItemIdCurrentBook(anyLong(), any())).thenReturn(Optional.of(booking));
        Booking lastBooking = bookingService.lastBookingForItem(item.getId());
        assertEquals(booking, lastBooking);
        verify(bookingRepository).findByItemIdCurrentBook(eq(item.getId()), any(LocalDateTime.class));
    }


    @Test
    void nextBookingForItem() {
        when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(anyLong(), any())).thenReturn(Optional.of(booking));
        Booking nextBooking = bookingService.nextBookingForItem(item.getId());
        assertEquals(booking, nextBooking);
        verify(bookingRepository).findFirstByItemIdAndStartAfterOrderByStartAsc(eq(item.getId()), any(LocalDateTime.class));
    }

    @Test
    void lastBookingForItemWhenNoCurrentBooking() {
        when(bookingRepository.findByItemIdCurrentBook(anyLong(), any())).thenReturn(Optional.empty());
        Booking lastBooking = bookingService.lastBookingForItem(item.getId());
        assertNull(lastBooking);
        verify(bookingRepository).findByItemIdCurrentBook(eq(item.getId()), any(LocalDateTime.class));
    }

    @Test
    void createBookingWhenUserNotFound() {
        when(userService.getUserById(user.getId())).thenThrow(new InvalidParameterForBooking("Пользователь не найден"));

        assertThrows(InvalidParameterForBooking.class, () -> bookingService.createBooking(user.getId(), bookingDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void nextBookingForItemWhenNoNextBooking() {
        when(bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(anyLong(), any())).thenReturn(Optional.empty());

        Booking nextBooking = bookingService.nextBookingForItem(item.getId());

        assertNull(nextBooking);
        verify(bookingRepository).findFirstByItemIdAndStartAfterOrderByStartAsc(eq(item.getId()), any(LocalDateTime.class));
    }
}
