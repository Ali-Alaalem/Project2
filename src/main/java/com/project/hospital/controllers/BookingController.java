package com.project.hospital.controllers;


import com.project.hospital.dto.BookingCreateRequest;
import com.project.hospital.dto.BookingResponse;
import com.project.hospital.models.Booking;
import com.project.hospital.repositorys.UserRepository;
import com.project.hospital.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserRepository userRepository; // Add this

    @Autowired
    public BookingController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('booking:create')")
    public BookingResponse createBooking(@RequestBody BookingCreateRequest bookingRequest) {
        return bookingService.createBooking(bookingRequest);
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('booking:view')")
    public List<BookingResponse> findAll() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAuthority('booking:view')")
    public BookingResponse findBookingById(@PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingById(bookingId);
    }


    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasAuthority('booking:delete')")
    public BookingResponse deleteBookingById(@PathVariable("bookingId") Long bookingId) {
        return bookingService.deleteBookingById(bookingId);
    }


    @PutMapping("/{bookingId}")
    @PreAuthorize("hasAuthority('booking:update')")
    public BookingResponse updateBooking(@PathVariable("bookingId") Long bookingId, @RequestBody Booking booking) {
        return bookingService.updateBooking(bookingId, booking);
    }
}
