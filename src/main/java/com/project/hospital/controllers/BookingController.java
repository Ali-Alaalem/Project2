package com.project.hospital.controllers;


import com.project.hospital.models.Booking;
import com.project.hospital.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('booking:create')")
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('booking:view')")
    public List<Booking> findAll() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAuthority('booking:view')")
    public Booking findBookingById(@PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }


    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAuthority('booking:delete')")
    public Booking deleteBookingById(@PathVariable("bookingId") Long bookingId) {
        return bookingService.deleteBookingById(bookingId);
    }


    @PutMapping("/{bookingId}")
    @PreAuthorize("hasAuthority('booking:update')")
    public Booking updateBooking(@PathVariable("bookingId") Long bookingId, @RequestBody Booking booking) {
        return bookingService.updateBooking(bookingId, booking);
    }
}
