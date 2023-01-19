package com.istratenkov.energyregistration.controller;

import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.model.entity.enumeration.Month;
import com.istratenkov.energyregistration.repository.AbstractRepositoryTest;
import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {LegacyUploadControllerTest.Initializer.class})
//@ActiveProfiles("with-db-test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LegacyUploadControllerTest extends AbstractRepositoryTest {

    @Autowired
    LegacyUploadController legacyUploadController;
    @Autowired
    ProfileRepository profileRepository;
    @Autowired
    FractionRepository fractionRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    void uploadFractions() throws IOException, DataFormatException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("testValidFractionsUpload.csv");
        File file = new File(url.getPath());
        InputStream inputStream = new FileInputStream(file);
        MockMultipartFile mockMultipartFile =  new MockMultipartFile(
                "testValidFractionsUpload.csv", inputStream);
        ResponseEntity<Object> objectResponseEntity = legacyUploadController.uploadFractions(mockMultipartFile);
        assertEquals(ResponseEntity.ok().build(), objectResponseEntity);
        Profile savedProfile = profileRepository.findAllByNameIn(List.of("A")).get(0);
        assertEquals("A", savedProfile.getName());
        assertNotNull(savedProfile.getId());
        List<Fraction> savedFractions = fractionRepository.findAllByProfileIdAndYear(savedProfile.getId(), 2023);
        int year = 2023;
        Set<Fraction> expectedFractions = new HashSet<>();
        expectedFractions.add(new Fraction(1L, 0.2f,  Month.JAN, year, savedProfile));
        expectedFractions.add(new Fraction(2L, 0.18f, Month.FEB, year, savedProfile));
        expectedFractions.add(new Fraction(3L, 0.05f, Month.JUL, year, savedProfile));
        expectedFractions.add(new Fraction(4L, 0.21f, Month.MAR, year, savedProfile));
        expectedFractions.add(new Fraction(5L, 0.04f, Month.APR, year, savedProfile));
        expectedFractions.add(new Fraction(6L, 0.04f, Month.JUN, year, savedProfile));
        expectedFractions.add(new Fraction(7L, 0.04f, Month.AUG, year, savedProfile));
        expectedFractions.add(new Fraction(8L, 0.05f, Month.SEP, year, savedProfile));
        expectedFractions.add(new Fraction(9L, 0.01f, Month.OCT, year, savedProfile));
        expectedFractions.add(new Fraction(10L, 0.08f, Month.NOV, year, savedProfile));
        expectedFractions.add(new Fraction(11L, 0.05f, Month.DEC, year, savedProfile));
        expectedFractions.add(new Fraction(12L, 0.05f, Month.MAY, year, savedProfile));
        savedFractions.forEach(e -> assertTrue(expectedFractions.contains(e)));
    }

    @Test
    void uploadMeasurements() {
    }

    @Test
    void getConsumptionForMeter() {
    }
}