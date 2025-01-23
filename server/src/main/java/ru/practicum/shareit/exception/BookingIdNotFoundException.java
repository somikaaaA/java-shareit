package ru.practicum.shareit.exception;

public class BookingIdNotFoundException extends RuntimeException {
    public BookingIdNotFoundException(String message) {
        super(message);
    }
}
