package ru.practicum.shareit.exception;

public class InvalidBookingFieldException extends RuntimeException {
    public InvalidBookingFieldException(String message) {
        super(message);
    }
}