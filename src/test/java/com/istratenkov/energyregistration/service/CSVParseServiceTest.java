package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.model.entity.enumeration.Month;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CSVParseServiceTest {

    CSVParseService csvParseService = new CSVParseService();

    @Test
    void parseFractionsFromFile() throws IOException, DataFormatException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("testValidFractionsUpload.csv");
        File file = new File(url.getPath());
        InputStream inputStream = new FileInputStream(file);
        MockMultipartFile mockMultipartFile =  new MockMultipartFile(
                "testValidFractionsUpload.csv", inputStream);
        Map<Profile, List<Fraction>> parsedProfileFractions = csvParseService.parseFractionsFromFile(mockMultipartFile);

        Profile profile = new Profile();
        profile.setName("A");
        Profile parsedProfile = parsedProfileFractions.keySet().iterator().next();
        assertEquals(profile, parsedProfile);
        List<Fraction> parsedFractions = parsedProfileFractions.get(parsedProfile);
        List<Fraction> expectedFractions = new ArrayList<>();
        Integer year = 2023;
        expectedFractions.add(new Fraction(0.2f,  Month.JAN, year, profile));
        expectedFractions.add(new Fraction(0.18f, Month.FEB, year, profile));
        expectedFractions.add(new Fraction(0.05f, Month.JUL, year, profile));
        expectedFractions.add(new Fraction(0.21f, Month.MAR, year, profile));
        expectedFractions.add(new Fraction(0.04f, Month.APR, year, profile));
        expectedFractions.add(new Fraction(0.04f, Month.JUN, year, profile));
        expectedFractions.add(new Fraction(0.04f, Month.AUG, year, profile));
        expectedFractions.add(new Fraction(0.05f, Month.SEP, year, profile));
        expectedFractions.add(new Fraction(0.01f, Month.OCT, year, profile));
        expectedFractions.add(new Fraction(0.08f, Month.NOV, year, profile));
        expectedFractions.add(new Fraction(0.05f, Month.DEC, year, profile));
        expectedFractions.add(new Fraction(0.05f, Month.MAY, year, profile));
        assertEquals(expectedFractions, parsedFractions);
    }

    @Test
    void parseMeterMeasurementsFromFile() throws IOException, DataFormatException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("testValidMeasurementsUpload.csv");
        File file = new File(url.getPath());
        InputStream inputStream = new FileInputStream(file);
        MockMultipartFile mockMultipartFile =  new MockMultipartFile(
                "testValidFractionsUpload.csv", inputStream);
        Map<Profile, List<MeterMeasurement>> parsedProfileFractions = csvParseService.parseMeterMeasurementsFromFile(mockMultipartFile);

        Profile profile = new Profile();
        profile.setName("A");
        profile.setMeterId("0001");
        Profile parsedProfile = parsedProfileFractions.keySet().iterator().next();
        assertEquals(profile, parsedProfile);
        List<MeterMeasurement> parsedFractions = parsedProfileFractions.get(parsedProfile);
        List<MeterMeasurement> expectedFractions = new ArrayList<>();
        Integer year = 2023;
        expectedFractions.add(new MeterMeasurement(52, Month.JAN, year));
        expectedFractions.add(new MeterMeasurement(101, Month.FEB, year));
        expectedFractions.add(new MeterMeasurement(201, Month.JUL, year));
        expectedFractions.add(new MeterMeasurement(155, Month.MAR, year));
        expectedFractions.add(new MeterMeasurement(165, Month.APR, year));
        expectedFractions.add(new MeterMeasurement(188, Month.JUN, year));
        expectedFractions.add(new MeterMeasurement(211, Month.AUG, year));
        expectedFractions.add(new MeterMeasurement(224, Month.SEP, year));
        expectedFractions.add(new MeterMeasurement(226, Month.OCT, year));
        expectedFractions.add(new MeterMeasurement(247, Month.NOV, year));
        expectedFractions.add(new MeterMeasurement(260, Month.DEC, year));
        expectedFractions.add(new MeterMeasurement(178, Month.MAY, year));
        assertEquals(expectedFractions, parsedFractions);
    }
}