package com.istratenkov.energyregistration.service.impl;

import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadServiceImplTest {
    @Mock
    CSVParseServiceImpl csvParseService;
    @Mock
    FractionServiceImpl fractionService;
    @Mock
    ProfileServiceImpl profileService;
    @Mock
    MeasurementServiceImpl measurementService;
    @InjectMocks
    UploadServiceImpl uploadService;

    @SneakyThrows
    @Test
    void uploadFractions() {
        MultipartFile file = generateFile();
        Map<Profile, List<Fraction>> profileFractionsParsed = new HashMap<>();
        Set<Profile> invalidProfiles = new HashSet<>();
        List<Profile>validProfiles = new ArrayList<>();
        ValidationResultDto resultDto = new ValidationResultDto(invalidProfiles, validProfiles);
        when(csvParseService.parseFractionsFromFile(file)).thenReturn(profileFractionsParsed);
        when(fractionService.validateParsedFractions(profileFractionsParsed)).thenReturn(resultDto);
        doNothing().when(fractionService).saveFractionsWithProfile(validProfiles);

        ValidationResultDto result = uploadService.uploadFractions(file);

        assertEquals(resultDto, result);
        verify(csvParseService).parseFractionsFromFile(file);
        verify(fractionService).validateParsedFractions(profileFractionsParsed);
        verify(fractionService).saveFractionsWithProfile(validProfiles);
    }

    @SneakyThrows
    @Test
    void uploadMeasurements() {
        MultipartFile file = generateFile();
        Map<Profile, List<MeterMeasurement>> parsedMeasurements = new HashMap<>();
        List<MeterMeasurement> meterMeasurements = new ArrayList<>();
        meterMeasurements.add(new MeterMeasurement());
        Profile profile = new Profile();
        profile.setMeasurements(meterMeasurements);
        Set<Profile> invalidProfiles = new HashSet<>();
        List<Profile>validProfiles = new ArrayList<>();
        validProfiles.add(profile);
        ValidationResultDto resultDto = new ValidationResultDto(invalidProfiles, validProfiles);
        when(csvParseService.parseMeterMeasurementsFromFile(file)).thenReturn(parsedMeasurements);
        when(measurementService.validateParsedMeasurements(parsedMeasurements)).thenReturn(resultDto);

        uploadService.uploadMeasurements(file);

        verify(csvParseService).parseMeterMeasurementsFromFile(file);
        verify(measurementService).validateParsedMeasurements(parsedMeasurements);
        verify(measurementService).saveAll(anyList());
        verify(profileService).saveAll(validProfiles);
    }

    private MockMultipartFile generateFile() {
        return new MockMultipartFile("file",
                "data.csv",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "data".getBytes());
    }
}