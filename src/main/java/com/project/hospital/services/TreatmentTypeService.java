package com.project.hospital.services;


import com.project.hospital.exceptions.InformationExistException;
import com.project.hospital.exceptions.InformationNotFoundException;
import com.project.hospital.models.TreatmentType;
import com.project.hospital.repositorys.TreatmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreatmentTypeService {
    private final TreatmentTypeRepository treatmentTypeRepository;

    @Autowired
    public TreatmentTypeService(TreatmentTypeRepository treatmentTypeRepository) {
        this.treatmentTypeRepository = treatmentTypeRepository;
    }

    public TreatmentType createTreatmentType(TreatmentType treatmentType) {
        if (treatmentType.getType() == null) {
            throw new RuntimeException("Treatment type is empty");
        }


        if (treatmentTypeRepository.existsByType(treatmentType.getType())) {
            throw new InformationExistException("Type already exists");
        }

        return treatmentTypeRepository.save(treatmentType);
    }

    public List<TreatmentType> findAllTreatmentType() {
        return treatmentTypeRepository.findAll();
    }


    public TreatmentType findTreatmentTypeByType(String type) {
        return treatmentTypeRepository.findByType(type).orElseThrow(
                () -> new InformationNotFoundException("Treatment type does not exist")
        );
    }

    public TreatmentType findTreatmentTypeById(Long id) {
        return treatmentTypeRepository.findById(id).orElseThrow(
                () -> new InformationNotFoundException("Treatment type with id does not exist")
        );
    }

    public TreatmentType deleteTreatmentTypeById(Long id) {
        TreatmentType currentTreatmentType = treatmentTypeRepository.findById(id).orElseThrow(
                () -> new InformationNotFoundException("Treatment type does not exist")
        );
        treatmentTypeRepository.delete(currentTreatmentType);
        return currentTreatmentType;
    }


    public TreatmentType updateTreatmentTypeById(Long id, TreatmentType treatmentType) {
        TreatmentType currentTreatmentType = treatmentTypeRepository.findById(id).orElseThrow(
                () -> new InformationNotFoundException("Treatment type does not exist")
        );

        if (treatmentType.getType() != null) {
            currentTreatmentType.setType(treatmentType.getType());
        }

        return  treatmentTypeRepository.save(currentTreatmentType);
    }
}
