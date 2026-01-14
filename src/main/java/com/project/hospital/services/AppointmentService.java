package com.project.hospital.services;

import com.project.hospital.exceptions.InformationExistException;
import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.Appointment;
import com.project.hospital.models.Booking;
import com.project.hospital.models.User;
import com.project.hospital.repositorys.AppointmentRepository;
import com.project.hospital.repositorys.BookingRepository;
import com.project.hospital.repositorys.UserRepository;
import com.project.hospital.models.response.BookAppointmentResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public Appointment createAppointment(Appointment appointment) {
        validateAppointment(appointment);

        // Ensure no time conflict for doctor (using ID now)
        if (appointmentRepository.existsByDoctorIdAndStartTimeBetween(
                appointment.getDoctor().getId(),
                appointment.getStartTime(),
                appointment.getEndTime().minusNanos(1))) {
            throw new InformationExistException("Doctor is already booked during this time");
        }

        // Ensure no time conflict for room
        if (appointmentRepository.existsByRoomIdAndStartTimeBetween(
                appointment.getRoom().getId(),
                appointment.getStartTime(),
                appointment.getEndTime().minusNanos(1))) {
            throw new InformationExistException("Room is already booked during this time");
        }

        return appointmentRepository.save(appointment);
    }

    // READ - all
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // READ - all doctor appointments (only users with DOCTOR role)
    public List<Appointment> getAllDoctorAppointments() {
        return appointmentRepository.findAllDoctorAppointments();
    }

    // READ - by ID
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Appointment with id " + id + " not found"));
    }

    // READ - by doctor ID (with role verification)
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        // Verify user exists and is a doctor
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new InformationNotFoundException("Doctor with id " + doctorId + " not found"));

        if (!"DOCTOR".equals(doctor.getRole().getName())) {
            throw new IllegalArgumentException("User with id " + doctorId + " is not a doctor");
        }

        return appointmentRepository.findByDoctorId(doctorId);
    }

    // Alternative: Use query with built-in role check
    public List<Appointment> getAppointmentsByDoctorIdVerified(Long doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdWithRoleCheck(doctorId);

        if (appointments.isEmpty()) {
            // Check if user exists at all
            if (!userRepository.existsById(doctorId)) {
                throw new InformationNotFoundException("Doctor with id " + doctorId + " not found");
            }
            // If exists but no appointments, could mean they're not a doctor or have no appointments
        }

        return appointments;
    }

    // READ - by room
    public List<Appointment> getAppointmentsByRoomId(Long roomId) {
        return appointmentRepository.findByRoomId(roomId);
    }

    public List<Appointment> getRoomAppointmentsForAvailability(Long roomId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findRoomAppointmentsWithDoctorAndBooking(roomId, start, end);
    }

    // READ - by booking
    public List<Appointment> getAppointmentsByBookingId(Long bookingId) {
        return appointmentRepository.findByBookingId(bookingId);
    }

    // UPDATE
    public Appointment updateAppointment(Long id, Appointment updates) {
        Appointment existing = getAppointmentById(id);

        // Validate updated data
        validateAppointment(updates);

        // Prevent changing to a conflicting time/slot
        if (!existing.getDoctor().getId().equals(updates.getDoctor().getId()) ||
                !existing.getRoom().getId().equals(updates.getRoom().getId()) ||
                !existing.getStartTime().equals(updates.getStartTime()) ||
                !existing.getEndTime().equals(updates.getEndTime())) {

            // Check new time slot conflicts (using IDs now)
            if (appointmentRepository.existsByDoctorIdAndStartTimeBetween(
                    updates.getDoctor().getId(),
                    updates.getStartTime(),
                    updates.getEndTime().minusNanos(1))) {
                throw new InformationExistException("Doctor is already booked during this time");
            }

            if (appointmentRepository.existsByRoomIdAndStartTimeBetween(
                    updates.getRoom().getId(),
                    updates.getStartTime(),
                    updates.getEndTime().minusNanos(1))) {
                throw new InformationExistException("Room is already booked during this time");
            }
        }

        // Apply updates
        existing.setDoctor(updates.getDoctor());
        existing.setRoom(updates.getRoom());
        existing.setBooking(updates.getBooking());
        existing.setStartTime(updates.getStartTime());
        existing.setEndTime(updates.getEndTime());

        return appointmentRepository.save(existing);
    }

    // DELETE
    public Appointment deleteAppointmentById(Long id) {
        Appointment existingAppointment = appointmentRepository.findById(id).orElseThrow(
                () -> new InformationNotFoundException("Appointment with id " + id + " not found")
        );
        appointmentRepository.delete(existingAppointment);
        return existingAppointment;
    }

    public BookAppointmentResponse bookAppointment(Long appointmentId, String patientEmail) {
        Appointment appointment = getAppointmentById(appointmentId);

        if (appointment.getBooking() != null) {
            throw new InformationExistException("Appointment is already booked");
        }

        if (appointment.getStartTime() != null && appointment.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book an appointment in the past");
        }

        User patient = userRepository.findUserByEmailAddress(patientEmail);
        if (patient == null) {
            throw new InformationNotFoundException("Patient not found");
        }
        if (patient.getRole() == null || !"PATIENT".equals(patient.getRole().getName())) {
            throw new IllegalArgumentException("Only PATIENT users can book appointments");
        }

        Booking booking = new Booking();
        booking.setBookedAt(LocalDateTime.now());
        booking.setPatient(patient);
        Booking savedBooking = bookingRepository.save(booking);

        appointment.setBooking(savedBooking);
        appointmentRepository.save(appointment);

        return new BookAppointmentResponse(
                savedBooking.getId(),
                appointment.getId(),
                savedBooking.getBookedAt(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );
    }

    private void validateAppointment(Appointment appointment) {
        if (appointment.getDoctor() == null) {
            throw new IllegalArgumentException("Doctor is required");
        }

        // Verify the doctor is actually a doctor
        if (appointment.getDoctor().getId() != null) {
            User doctor = userRepository.findById(appointment.getDoctor().getId())
                    .orElseThrow(() -> new InformationNotFoundException("Doctor not found"));

            if (!"DOCTOR".equals(doctor.getRole().getName())) {
                throw new IllegalArgumentException("User must have DOCTOR role");
            }
        }

        if (appointment.getRoom() == null) {
            throw new IllegalArgumentException("Room is required");
        }
        if (appointment.getStartTime() == null || appointment.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }
        if (!appointment.getStartTime().isBefore(appointment.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }
    }
}