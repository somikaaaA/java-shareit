package ru.practicum.shareit.exception;

public class ItemIdNotFoundException  extends RuntimeException {
    public ItemIdNotFoundException(String message) {
        super(message);
    }
}
