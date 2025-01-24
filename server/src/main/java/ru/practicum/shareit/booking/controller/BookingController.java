package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.validate.UserIdValid;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@UserIdValid @RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody BookingDto bookingDto) {

        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto addApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long bookingId,
                                 @RequestParam(name = "approved") boolean approved) {
        return bookingService.createApprove(userId, bookingId, approved);
    }

    @GetMapping("/owner")
    public List<BookingDto> searchBookingsForOwner(@UserIdValid @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.searchBookingsForOwner(ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto searchBookingByIdForOwnerOrForBooker(@UserIdValid @RequestHeader("X-Sharer-User-Id") Long userId,
                                                           @PathVariable Long bookingId) {
        return bookingService.searchBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> searchBookingForUserWithState(@UserIdValid @RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(name = "state", defaultValue = "ALL") Status state) {
        return bookingService.searchBookingsWithState(userId, state);
    }
}
