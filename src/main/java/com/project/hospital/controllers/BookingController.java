package com.project.hospital.controllers;


import com.project.hospital.models.Booking;
import com.project.hospital.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/")
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @GetMapping("/")
    public List<Booking> findAll() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{bookingId}")
    public Booking findBookingById(@PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }


    @DeleteMapping("/{bookingId}")
    public Booking deleteBookingById(@PathVariable("bookingId") Long bookingId) {
        return bookingService.deleteBookingById(bookingId);
    }


    @PutMapping("/{bookingId}")
    public Booking updateBooking(@PathVariable("bookingId") Long bookingId, @RequestBody Booking booking) {
        return bookingService.updateBooking(bookingId, booking);
    }
}
