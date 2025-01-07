package ru.practicum.shareit.exception;

public class InvalidItemIdException  extends RuntimeException {
    public InvalidItemIdException(String message) {
        super(message);
    }
}
