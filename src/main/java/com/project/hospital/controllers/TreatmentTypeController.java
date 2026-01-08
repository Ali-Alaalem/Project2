package com.project.hospital.controllers;

import com.project.hospital.models.TreatmentType;
import com.project.hospital.services.TreatmentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(name = "/api/treatmentTypes")
public class TreatmentTypeController {

    private final TreatmentTypeService treatmentTypeService;

    @Autowired
    public TreatmentTypeController(TreatmentTypeService treatmentTypeService) {
        this.treatmentTypeService = treatmentTypeService;
    }

    @PostMapping("/")
    public TreatmentType createTreatmentType(@RequestBody TreatmentType treatmentType) {
        return treatmentTypeService.createTreatmentType(treatmentType);
    }

    @GetMapping("/")
    public List<TreatmentType> getAllTreatmentTypes() {
        return treatmentTypeService.findAllTreatmentType();
    }

    @GetMapping("/{treatmentTypeId}")
    public TreatmentType getTreatmentTypeById(@PathVariable("treatmentTypeId") Long treatmentTypeId) {
        return treatmentTypeService.findTreatmentTypeById(treatmentTypeId);
    }

    @DeleteMapping("/{treatmentTypeId}")
    public TreatmentType deleteTreatmentTypeById(@PathVariable("treatmentTypeId") Long treatmentTypeId) {
        return treatmentTypeService.deleteTreatmentTypeById(treatmentTypeId);
    }

    @PutMapping("/{treatmentTypeId}")
    public TreatmentType updateTreatmentTypeById(@PathVariable("treatmentTypeId") Long treatmentTypeId, @RequestBody TreatmentType treatmentType) {
        return treatmentTypeService.updateTreatmentTypeById(treatmentTypeId, treatmentType);
    }
}
