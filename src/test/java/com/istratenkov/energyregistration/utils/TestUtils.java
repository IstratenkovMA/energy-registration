package com.istratenkov.energyregistration.utils;

import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.model.entity.enumeration.Month;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    public static List<MeterMeasurement> generateMeasurementsTestData() {
        List<MeterMeasurement> measurements = new ArrayList<>();
        Integer year = 2023;
        measurements.add(new MeterMeasurement(52, Month.JAN, year));
        measurements.add(new MeterMeasurement(101, Month.FEB, year));
        measurements.add(new MeterMeasurement(201, Month.JUL, year));
        measurements.add(new MeterMeasurement(155, Month.MAR, year));
        measurements.add(new MeterMeasurement(165, Month.APR, year));
        measurements.add(new MeterMeasurement(188, Month.JUN, year));
        measurements.add(new MeterMeasurement(211, Month.AUG, year));
        measurements.add(new MeterMeasurement(224, Month.SEP, year));
        measurements.add(new MeterMeasurement(226, Month.OCT, year));
        measurements.add(new MeterMeasurement(247, Month.NOV, year));
        measurements.add(new MeterMeasurement(260, Month.DEC, year));
        measurements.add(new MeterMeasurement(178, Month.MAY, year));
        return measurements;
    }

    public static List<Fraction> generateFractionsTestData(Profile profile) {
        List<Fraction> fractions = new ArrayList<>();
        Integer year = 2023;
        fractions.add(new Fraction(0.2f,  Month.JAN, year, profile));
        fractions.add(new Fraction(0.18f, Month.FEB, year, profile));
        fractions.add(new Fraction(0.05f, Month.JUL, year, profile));
        fractions.add(new Fraction(0.21f, Month.MAR, year, profile));
        fractions.add(new Fraction(0.04f, Month.APR, year, profile));
        fractions.add(new Fraction(0.04f, Month.JUN, year, profile));
        fractions.add(new Fraction(0.04f, Month.AUG, year, profile));
        fractions.add(new Fraction(0.05f, Month.SEP, year, profile));
        fractions.add(new Fraction(0.01f, Month.OCT, year, profile));
        fractions.add(new Fraction(0.08f, Month.NOV, year, profile));
        fractions.add(new Fraction(0.05f, Month.DEC, year, profile));
        fractions.add(new Fraction(0.05f, Month.MAY, year, profile));
        return fractions;
    }

    public static boolean compareMeasurementWithoutId(MeterMeasurement first, MeterMeasurement second) {
        if(!first.getValue().equals(second.getValue())) return false;
        if(!first.getMonth().equals(second.getMonth())) return false;
        if(!first.getYear().equals(second.getYear())) return false;
        if(!first.getProfile().equals(second.getProfile())) return false;
        return true;
    }

    public static boolean compareFractionWithoutId(Fraction first, Fraction second) {
        if(first.getValue() != second.getValue()) return false;
        if(!first.getMonth().equals(second.getMonth())) return false;
        if(!first.getYear().equals(second.getYear())) return false;
        if(!first.getProfile().equals(second.getProfile())) return false;
        return true;
    }
}
