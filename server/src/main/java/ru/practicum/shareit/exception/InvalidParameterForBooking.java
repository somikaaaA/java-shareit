package ru.practicum.shareit.exception;

public class InvalidParameterForBooking extends RuntimeException {
    public InvalidParameterForBooking(String message) {
        super(message);
    }
}