package ru.practicum.shareit.exception;

public class RequestIdNotFoundException extends RuntimeException {
    public RequestIdNotFoundException(String message) {
        super(message);
    }
}
