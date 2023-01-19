package com.istratenkov.energyregistration.model.entity.enumeration;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class MonthConverter implements AttributeConverter<Month, Integer> {
    public final static Map<Integer, Month> map = Arrays.stream(Month.values())
            .collect(Collectors.toMap(Month::getNumber, e -> e));

    @Override
    public Integer convertToDatabaseColumn(Month month) {
        if (month == null) {
            return null;
        }
        return month.getNumber();
    }

    @Override
    public Month convertToEntityAttribute(Integer code) {
        if (code == null) {
            return null;
        }
        return map.get(code);
    }
}
