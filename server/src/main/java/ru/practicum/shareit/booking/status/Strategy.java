package ru.practicum.shareit.booking.status;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface Strategy {
    List<Booking> searchBookings(Long id);

    Status getStatusName();
}
