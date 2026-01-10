package com.project.hospital.controllers;


import com.project.hospital.models.Appointment;
import com.project.hospital.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Appointment createAppointment(@RequestBody Appointment appointment) {
        return appointmentService.createAppointment(appointment);
    }


    @GetMapping("/")
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{appointmentId}")
    public Appointment getAppointmentById(@PathVariable("appointmentId") Long appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    @DeleteMapping("/{appointmentId}")
    public Appointment deleteAppointmentById(@PathVariable("appointmentId") Long appointmentId) {
        return appointmentService.deleteAppointmentById(appointmentId);
    }


    @PutMapping("/{appointmentId}")
    public Appointment updateAppointmentById(@PathVariable("appointmentId") Long appointmentId, @RequestBody Appointment appointment) {
        return appointmentService.updateAppointment(appointmentId, appointment);
    }
}
