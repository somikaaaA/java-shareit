package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.status.Status;


import java.util.List;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingDto bookingDto);

    BookingDto createApprove(Long userId, Long bookingId, boolean approved);

    List<BookingDto> searchBookingsForOwner(Long ownerId);

    BookingDto searchBooking(Long userId, Long bookingId);

    List<BookingDto> searchBookingsWithState(Long userId, Status state);

    Booking lastBookingForItem(Long id);

    Booking nextBookingForItem(Long id);
}
