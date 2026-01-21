package com.project.hospital.controllers;

import com.project.hospital.models.TreatmentType;
import com.project.hospital.services.TreatmentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treatmentTypes")
public class TreatmentTypeController {

    private final TreatmentTypeService treatmentTypeService;

    @Autowired
    public TreatmentTypeController(TreatmentTypeService treatmentTypeService) {
        this.treatmentTypeService = treatmentTypeService;
    }

    @PostMapping("/")
    @PreAuthorize("hasAuthority('treatmenttype:create')")
    public TreatmentType createTreatmentType(@RequestBody TreatmentType treatmentType) {
        return treatmentTypeService.createTreatmentType(treatmentType);
    }

    @GetMapping("/")
    @PreAuthorize("hasAuthority('treatmenttype:view')")
    public List<TreatmentType> getAllTreatmentTypes() {
        return treatmentTypeService.findAllTreatmentType();
    }

    @GetMapping("/{treatmentTypeId}")
    @PreAuthorize("hasAuthority('treatmenttype:view')")
    public TreatmentType getTreatmentTypeById(@PathVariable("treatmentTypeId") Long treatmentTypeId) {
        return treatmentTypeService.findTreatmentTypeById(treatmentTypeId);
    }

    @DeleteMapping("/{treatmentTypeId}")
    @PreAuthorize("hasAuthority('treatmenttype:delete')")
    public ResponseEntity<Void> deleteTreatmentTypeById(@PathVariable("treatmentTypeId") Long treatmentTypeId) {
        treatmentTypeService.deleteTreatmentTypeById(treatmentTypeId);
        return ResponseEntity.noContent().build();

    }

    @PutMapping("/{treatmentTypeId}")
    @PreAuthorize("hasAuthority('treatmenttype:update')")
    public TreatmentType updateTreatmentTypeById(@PathVariable("treatmentTypeId") Long treatmentTypeId, @RequestBody TreatmentType treatmentType) {
        return treatmentTypeService.updateTreatmentTypeById(treatmentTypeId, treatmentType);
    }
}
