package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CSVParseServiceImplTest {

    CSVParseServiceImpl csvParseService = new CSVParseServiceImpl();

    @Test
    void parseFractionsFromFile() throws IOException, DataFormatException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("testValidFractionsUpload.csv");
        File file = new File(url.getPath());
        InputStream inputStream = new FileInputStream(file);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "testValidFractionsUpload.csv", inputStream);
        Map<Profile, List<Fraction>> parsedProfileFractions = csvParseService.parseFractionsFromFile(mockMultipartFile);

        Profile profile = new Profile();
        profile.setName("A");
        Profile parsedProfile = parsedProfileFractions.keySet().iterator().next();
        assertEquals(profile, parsedProfile);
        List<Fraction> parsedFractions = parsedProfileFractions.get(parsedProfile);
        List<Fraction> expectedFractions = TestUtils.generateFractionsTestData(profile);
        assertEquals(expectedFractions, parsedFractions);
    }

    @Test
    void parseMeterMeasurementsFromFile() throws IOException, DataFormatException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("testValidMeasurementsUpload.csv");
        File file = new File(url.getPath());
        InputStream inputStream = new FileInputStream(file);
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "testValidFractionsUpload.csv", inputStream);
        Map<Profile, List<MeterMeasurement>> parsedProfileFractions = csvParseService.parseMeterMeasurementsFromFile(mockMultipartFile);

        Profile profile = new Profile();
        profile.setName("A");
        profile.setMeterId("0001");
        Profile parsedProfile = parsedProfileFractions.keySet().iterator().next();
        assertEquals(profile, parsedProfile);
        List<MeterMeasurement> parsedFractions = parsedProfileFractions.get(parsedProfile);
        List<MeterMeasurement> expectedMeasurements = TestUtils.generateMeasurementsTestData();
        assertEquals(expectedMeasurements, parsedFractions);
    }
}