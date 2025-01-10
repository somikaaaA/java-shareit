package ru.practicum.shareit.booking.status;

public enum Status {
    WAITING, //новое, ожидает одобрения
    APPROVED, //подтверждено владельцем
    REJECTED, //отклонено владельцем
    CANCELED, //отменено создателем
    ALL, //все
    CURRENT, //текущие
    PAST, //прошедшие
    FUTURE; //будущие
}
