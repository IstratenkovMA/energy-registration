package com.istratenkov.energyregistration.model.entity.enumeration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MonthConverterTest {

    private MonthConverter monthConverter = new MonthConverter();

    @Test
    void testNumberToMonthMap() {
        Month JAN = MonthConverter.map.get(1);
        Month FEB = MonthConverter.map.get(2);
        Month MAR = MonthConverter.map.get(3);
        Month APR = MonthConverter.map.get(4);
        Month MAY = MonthConverter.map.get(5);
        Month JUN = MonthConverter.map.get(6);
        Month JUL = MonthConverter.map.get(7);
        Month AUG = MonthConverter.map.get(8);
        Month SEP = MonthConverter.map.get(9);
        Month OCT = MonthConverter.map.get(10);
        Month NOV = MonthConverter.map.get(11);
        Month DEC = MonthConverter.map.get(12);

        assertEquals(Month.JAN, JAN);
        assertEquals(Month.FEB, FEB);
        assertEquals(Month.MAR, MAR);
        assertEquals(Month.APR, APR);
        assertEquals(Month.MAY, MAY);
        assertEquals(Month.JUN, JUN);
        assertEquals(Month.JUL, JUL);
        assertEquals(Month.AUG, AUG);
        assertEquals(Month.SEP, SEP);
        assertEquals(Month.OCT, OCT);
        assertEquals(Month.NOV, NOV);
        assertEquals(Month.DEC, DEC);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12}) // all month numbers
    void convertToDatabaseColumn(int monthNum) {
        Month month = MonthConverter.map.get(monthNum);
        Integer converted = monthConverter.convertToDatabaseColumn(month);
        assertEquals(monthNum, converted);
    }

    @ParameterizedTest
    @EnumSource(Month.class)
    void convertToEntityAttribute(Month month) {
        Month converted = monthConverter.convertToEntityAttribute(month.getNumber());
        assertEquals(month, converted);
    }
}