package com.boardgo.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanConverter implements AttributeConverter<Boolean, String> {

    private final String Y = "Y";
    private final String N = "N";

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        return (attribute != null && attribute) ? Y : N;
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        return Y.equals(dbData);
    }
}
