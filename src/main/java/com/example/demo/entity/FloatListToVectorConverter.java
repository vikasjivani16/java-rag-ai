package com.example.demo.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class FloatListToVectorConverter implements AttributeConverter<List<Float>, String> {

    @Override
    public String convertToDatabaseColumn(List<Float> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }

        // Format: [1.0, 2.0, 3.5]
        return attribute.stream().map(String::valueOf).collect(Collectors.joining(", ", "[", "]"));
    }

    @Override
    public List<Float> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return List.of();
        }

        // Remove square brackets if present and split
        return Arrays.stream(dbData.replaceAll("[\\[\\]]", "").split(","))
                .map(String::trim)
                .map(Float::parseFloat)
                .collect(Collectors.toList());
    }
}
