package com.project.hospital.services;

import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.Booking;
import com.project.hospital.repositorys.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // CREATE
    public Booking createBooking(Booking booking) {
        if (booking.getPatient() == null) {
            throw new IllegalArgumentException("Patient is required");
        }
        if (booking.getBookedAt() == null) {
            throw new IllegalArgumentException("Booking time is required");
        }
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new InformationNotFoundException("Booking with id " + id + " not found"));
    }

    public List<Booking> getBookingsByPatientId(Long patientId) {
        return bookingRepository.findByPatientId(patientId);
    }

    public List<Booking> getBookingsByPatientIdAndTimeRange(Long patientId, LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByPatientIdAndBookedAtBetween(patientId, start, end);
    }

    public Booking deleteBookingById(Long id) {
        Booking currentBooking = bookingRepository.findById(id).orElseThrow(() -> new InformationNotFoundException("Booking with id " + id + " not found"));

        bookingRepository.delete(currentBooking);
        return currentBooking;
    }

    public Booking updateBooking(Long id, Booking bookingUpdates) {
        Booking existing = bookingRepository.findById(id).orElseThrow(
                () -> new InformationNotFoundException("Booking with id " + id + " not found")
        );

        if (bookingUpdates.getPatient() == null) {
            throw new IllegalArgumentException("Patient is required");
        }
        if (bookingUpdates.getBookedAt() == null) {
            throw new IllegalArgumentException("Booking time is required");
        }

        existing.setPatient(bookingUpdates.getPatient());
        existing.setBookedAt(bookingUpdates.getBookedAt());

        return bookingRepository.save(existing);
    }


}