package com.istratenkov.energyregistration.controller;

import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.model.entity.enumeration.Month;
import com.istratenkov.energyregistration.repository.AbstractRepositoryTest;
import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.MeterMeasurementRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
import com.istratenkov.energyregistration.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {LegacyUploadControllerIntegrationTest.Initializer.class})
//@ActiveProfiles("with-db-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class LegacyUploadControllerIntegrationTest extends AbstractRepositoryTest {

    @Autowired
    LegacyUploadController legacyUploadController;
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
    void uploadFractions() throws IOException, DataFormatException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("testValidFractionsUpload.csv");
        File file = new File(url.getPath());
        InputStream inputStream = new FileInputStream(file);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "testValidFractionsUpload.csv", inputStream);
        ResponseEntity<Object> objectResponseEntity = legacyUploadController.uploadFractions(mockMultipartFile);
        assertEquals(ResponseEntity.ok().build(), objectResponseEntity);
        Profile savedProfile = profileRepository.findAllByNameIn(List.of("A")).get(0);
        assertEquals("A", savedProfile.getName());
        assertNotNull(savedProfile.getId());
        List<Fraction> savedFractions = fractionRepository.findAllByProfileIdAndYear(savedProfile.getId(), 2023);
        int year = 2023;
        Map<Month, Fraction> expectedFractions = new HashMap<>();
        expectedFractions.put(Month.JAN, new Fraction(0.2f, Month.JAN, year, savedProfile));
        expectedFractions.put(Month.FEB, new Fraction(0.18f, Month.FEB, year, savedProfile));
        expectedFractions.put(Month.JUL, new Fraction(0.05f, Month.JUL, year, savedProfile));
        expectedFractions.put(Month.MAR, new Fraction(0.21f, Month.MAR, year, savedProfile));
        expectedFractions.put(Month.APR, new Fraction(0.04f, Month.APR, year, savedProfile));
        expectedFractions.put(Month.JUN, new Fraction(0.04f, Month.JUN, year, savedProfile));
        expectedFractions.put(Month.AUG, new Fraction(0.04f, Month.AUG, year, savedProfile));
        expectedFractions.put(Month.SEP, new Fraction(0.05f, Month.SEP, year, savedProfile));
        expectedFractions.put(Month.OCT, new Fraction(0.01f, Month.OCT, year, savedProfile));
        expectedFractions.put(Month.NOV, new Fraction(0.08f, Month.NOV, year, savedProfile));
        expectedFractions.put(Month.DEC, new Fraction(0.05f, Month.DEC, year, savedProfile));
        expectedFractions.put(Month.MAY, new Fraction(0.05f, Month.MAY, year, savedProfile));
        savedFractions.forEach(
                e -> assertTrue(TestUtils.compareFractionWithoutId(e, expectedFractions.get(e.getMonth()))));
    }

    @Test
    void uploadMeasurements() throws IOException, DataFormatException {
        insertTestDataForMeasurements();
        URL url = Thread.currentThread().getContextClassLoader().getResource("testValidMeasurementsUpload.csv");
        File file = new File(url.getPath());
        InputStream inputStream = new FileInputStream(file);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "testValidMeasurementsUpload.csv", inputStream);
        ResponseEntity<Object> objectResponseEntity = legacyUploadController.uploadMeasurements(mockMultipartFile);
        assertEquals(ResponseEntity.ok().build(), objectResponseEntity);
        Profile savedProfile = profileRepository.findAllByNameIn(List.of("A")).get(0);
        assertEquals("A", savedProfile.getName());
        assertEquals("0001", savedProfile.getMeterId());
        assertNotNull(savedProfile.getId());
        List<MeterMeasurement> savedMeasurements = meterMeasurementRepository.findAllByProfileIdAndYear(savedProfile.getId(), 2023);
        int year = 2023;
        Map<Month, MeterMeasurement> expectedMeasurements = new HashMap<>();
        expectedMeasurements.put(Month.JAN, new MeterMeasurement(1L, 52, Month.JAN, year, savedProfile));
        expectedMeasurements.put(Month.FEB, new MeterMeasurement(2L, 101, Month.FEB, year, savedProfile));
        expectedMeasurements.put(Month.JUL, new MeterMeasurement(3L, 201, Month.JUL, year, savedProfile));
        expectedMeasurements.put(Month.MAR, new MeterMeasurement(4L, 155, Month.MAR, year, savedProfile));
        expectedMeasurements.put(Month.APR, new MeterMeasurement(5L, 165, Month.APR, year, savedProfile));
        expectedMeasurements.put(Month.JUN, new MeterMeasurement(6L, 188, Month.JUN, year, savedProfile));
        expectedMeasurements.put(Month.AUG, new MeterMeasurement(7L, 211, Month.AUG, year, savedProfile));
        expectedMeasurements.put(Month.SEP, new MeterMeasurement(8L, 224, Month.SEP, year, savedProfile));
        expectedMeasurements.put(Month.OCT, new MeterMeasurement(9L, 226, Month.OCT, year, savedProfile));
        expectedMeasurements.put(Month.NOV, new MeterMeasurement(10L, 247, Month.NOV, year, savedProfile));
        expectedMeasurements.put(Month.DEC, new MeterMeasurement(11L, 260, Month.DEC, year, savedProfile));
        expectedMeasurements.put(Month.MAY, new MeterMeasurement(12L, 178, Month.MAY, year, savedProfile));
        savedMeasurements.forEach(
                e -> assertTrue(TestUtils.compareMeasurementWithoutId(e, expectedMeasurements.get(e.getMonth()))));
    }

    @Test
    void getConsumptionForMeter() {
        insertTestDataForConsumptionCalculation();
        ResponseEntity<Object> janConsumption = legacyUploadController.getConsumptionForMeter("0001", "JAN", 2023);
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

    private void insertTestDataForMeasurements() {
        int year = 2023;
        Profile profile = new Profile();
        profile.setName("A");
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
    }
}