package ru.practicum.shareit.exception;

public class InvalidBookingIdException extends RuntimeException {
    public InvalidBookingIdException(String message) {
        super(message);
    }
}
