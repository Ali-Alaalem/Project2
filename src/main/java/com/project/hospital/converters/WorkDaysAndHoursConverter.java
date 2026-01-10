package com.project.hospital.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalTime;
import java.util.HashMap;

@Converter
public class WorkDaysAndHoursConverter implements AttributeConverter<HashMap<String, HashMap<String, LocalTime>>, String> {
    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Override
    public String convertToDatabaseColumn(HashMap<String, HashMap<String, LocalTime>> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not serialize workDaysAndHours", e);
        }
    }

    @Override
    public HashMap<String, HashMap<String, LocalTime>> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new HashMap<>();
        }
        try {
            return mapper.readValue(dbData, new TypeReference<HashMap<String, HashMap<String, LocalTime>>>() {
            });
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not deserialize workDaysAndHours", e);
        }
    }
}
