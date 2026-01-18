package com.project.hospital.services;

import com.project.hospital.dto.BookingCreateRequest;
import com.project.hospital.dto.BookingResponse;
import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.Booking;
import com.project.hospital.models.User;
import com.project.hospital.repositorys.BookingRepository;
import com.project.hospital.repositorys.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    // CREATE
    public BookingResponse createBooking(BookingCreateRequest bookingRequest) {
        if (bookingRequest.getPatientId() == null) {
            throw new InformationNotFoundException("Patient id is required");
        }

        // Fetch the full patient entity from database
        User existingPatient = userRepository.findById(bookingRequest.getPatientId())
                .orElseThrow(() -> new InformationNotFoundException("Patient with id " + bookingRequest.getPatientId() + " not found"));

        if (bookingRequest.getBookedAt() == null) {
            throw new IllegalArgumentException("Booking time is required");
        }

        // Create new booking with the fetched patient
        Booking booking = new Booking();
        booking.setPatient(existingPatient);
        booking.setBookedAt(bookingRequest.getBookedAt());

        Booking savedBooking = bookingRepository.save(booking);
        
        // Return response with patient information
        return new BookingResponse(
            savedBooking.getId(),
            savedBooking.getBookedAt(),
            savedBooking.getPatient()
        );
    }

    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(booking -> new BookingResponse(
                        booking.getId(),
                        booking.getBookedAt(),
                        booking.getPatient()
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Booking with id " + id + " not found"));
        return new BookingResponse(
                booking.getId(),
                booking.getBookedAt(),
                booking.getPatient()
        );
    }

    public List<BookingResponse> getBookingsByPatientId(Long patientId) {
        List<Booking> bookings = bookingRepository.findByPatientId(patientId);
        return bookings.stream()
                .map(booking -> new BookingResponse(
                        booking.getId(),
                        booking.getBookedAt(),
                        booking.getPatient()
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<BookingResponse> getBookingsByPatientIdAndTimeRange(Long patientId, LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = bookingRepository.findByPatientIdAndBookedAtBetween(patientId, start, end);
        return bookings.stream()
                .map(booking -> new BookingResponse(
                        booking.getId(),
                        booking.getBookedAt(),
                        booking.getPatient()
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    public BookingResponse deleteBookingById(Long id) {
        Booking currentBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Booking with id " + id + " not found"));
        
        BookingResponse response = new BookingResponse(
                currentBooking.getId(),
                currentBooking.getBookedAt(),
                currentBooking.getPatient()
        );

        bookingRepository.delete(currentBooking);
        return response;
    }

    public BookingResponse updateBooking(Long id, Booking bookingUpdates) {
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

        Booking savedBooking = bookingRepository.save(existing);
        return new BookingResponse(
                savedBooking.getId(),
                savedBooking.getBookedAt(),
                savedBooking.getPatient()
        );
    }
}