package com.istratenkov.energyregistration.controller;

import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.model.entity.enumeration.Month;
import com.istratenkov.energyregistration.repository.AbstractRepositoryTest;
import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.MeterMeasurementRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {LegacyUploadControllerIntegrationTest.Initializer.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class QueryDataControllerTest extends AbstractRepositoryTest {

    @Autowired
    QueryDataController queryDataController;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    FractionRepository fractionRepository;
    @Autowired
    MeterMeasurementRepository meterMeasurementRepository;

    @BeforeEach
    void cleanUpTable() {
        fractionRepository.deleteAll();
        meterMeasurementRepository.deleteAll();
        profileRepository.deleteAll();
    }

    @Test
    void getConsumptionForMeter() {
        insertTestDataForConsumptionCalculation();
        ResponseEntity<Object> janConsumption = queryDataController.getConsumptionForMeter("0001", "JAN", 2023);
        assertEquals(52, (Integer) janConsumption.getBody());
    }

    private void insertTestDataForConsumptionCalculation() {
        int year = 2023;
        Profile profile = new Profile();
        profile.setName("A");
        profile.setMeterId("0001");
        Profile savedProfile = profileRepository.save(profile);
        List<Fraction> fractions = new ArrayList<>();
        fractions.add(new Fraction(1L, 0.2f, Month.JAN, year, savedProfile));
        fractions.add(new Fraction(2L, 0.18f, Month.FEB, year, savedProfile));
        fractions.add(new Fraction(3L, 0.05f, Month.JUL, year, savedProfile));
        fractions.add(new Fraction(4L, 0.21f, Month.MAR, year, savedProfile));
        fractions.add(new Fraction(5L, 0.04f, Month.APR, year, savedProfile));
        fractions.add(new Fraction(6L, 0.04f, Month.JUN, year, savedProfile));
        fractions.add(new Fraction(7L, 0.04f, Month.AUG, year, savedProfile));
        fractions.add(new Fraction(8L, 0.05f, Month.SEP, year, savedProfile));
        fractions.add(new Fraction(9L, 0.01f, Month.OCT, year, savedProfile));
        fractions.add(new Fraction(10L, 0.08f, Month.NOV, year, savedProfile));
        fractions.add(new Fraction(11L, 0.05f, Month.DEC, year, savedProfile));
        fractions.add(new Fraction(12L, 0.05f, Month.MAY, year, savedProfile));
        fractionRepository.saveAll(fractions);
        Set<MeterMeasurement> measurements = new HashSet<>();
        measurements.add(new MeterMeasurement(1L, 52, Month.JAN, year, savedProfile));
        measurements.add(new MeterMeasurement(2L, 101, Month.FEB, year, savedProfile));
        measurements.add(new MeterMeasurement(3L, 201, Month.JUL, year, savedProfile));
        measurements.add(new MeterMeasurement(4L, 155, Month.MAR, year, savedProfile));
        measurements.add(new MeterMeasurement(5L, 165, Month.APR, year, savedProfile));
        measurements.add(new MeterMeasurement(6L, 188, Month.JUN, year, savedProfile));
        measurements.add(new MeterMeasurement(7L, 211, Month.AUG, year, savedProfile));
        measurements.add(new MeterMeasurement(8L, 224, Month.SEP, year, savedProfile));
        measurements.add(new MeterMeasurement(9L, 226, Month.OCT, year, savedProfile));
        measurements.add(new MeterMeasurement(10L, 247, Month.NOV, year, savedProfile));
        measurements.add(new MeterMeasurement(11L, 260, Month.DEC, year, savedProfile));
        measurements.add(new MeterMeasurement(12L, 178, Month.MAY, year, savedProfile));
        meterMeasurementRepository.saveAll(measurements);
    }
}