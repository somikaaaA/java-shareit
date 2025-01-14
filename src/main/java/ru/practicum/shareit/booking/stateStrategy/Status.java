package ru.practicum.shareit.booking.stateStrategy;

public enum Status {
    WAITING, //новое бронирование, ожидает одобрения
    APPROVED, //бронирование подтверждено владельцем
    REJECTED, //бронирование отклонено владельцем
    CANCELED, //бронирование отменено создателем
    ALL, //все бронирования
    CURRENT, //текущие бронирования
    PAST, //прошедшие
    FUTURE; //будущие

}
