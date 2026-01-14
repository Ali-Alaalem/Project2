package com.project.hospital.controllers;


import com.project.hospital.models.Appointment;
import com.project.hospital.models.request.BookAppointmentRequest;
import com.project.hospital.models.response.BookAppointmentResponse;
import com.project.hospital.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('appointment:create')")
    public Appointment createAppointment(@RequestBody Appointment appointment) {
        return appointmentService.createAppointment(appointment);
    }


    @GetMapping("/")
    @PreAuthorize("hasAuthority('appointment:view')")
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAuthority('appointment:view')")
    public Appointment getAppointmentById(@PathVariable("appointmentId") Long appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    @DeleteMapping("/{appointmentId}")
    @PreAuthorize("hasAuthority('appointment:delete')")
    public Appointment deleteAppointmentById(@PathVariable("appointmentId") Long appointmentId) {
        return appointmentService.deleteAppointmentById(appointmentId);
    }


    @PutMapping("/{appointmentId}")
    @PreAuthorize("hasAuthority('appointment:update')")
    public Appointment updateAppointmentById(@PathVariable("appointmentId") Long appointmentId, @RequestBody Appointment appointment) {
        return appointmentService.updateAppointment(appointmentId, appointment);
    }

    @PostMapping("/book")
    @PreAuthorize("hasAuthority('booking:create')")
    public BookAppointmentResponse bookAppointment(Authentication authentication, @RequestBody BookAppointmentRequest request) {
        return appointmentService.bookAppointment(request.getAppointmentId(), authentication.getName());
    }
}
