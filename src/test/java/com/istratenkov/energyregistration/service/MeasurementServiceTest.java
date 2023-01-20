package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.model.entity.enumeration.Month;
import com.istratenkov.energyregistration.repository.MeterMeasurementRepository;
import com.istratenkov.energyregistration.service.impl.MeasurementServiceImpl;
import com.istratenkov.energyregistration.service.impl.ProfileServiceImpl;
import com.istratenkov.energyregistration.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {
    @Mock
    ProfileServiceImpl profileService;
    @Mock
    MeterMeasurementRepository measurementRepository;
    @InjectMocks
    MeasurementServiceImpl measurementService;

    @Test
    void validateParsedMeasurements_validMeasurements() {
        Profile profile = TestUtils.generateProfile(null, "profile", "0001");
        List<MeterMeasurement> measurements = TestUtils.generateMeasurementsTestData();
        Map<Profile, List<MeterMeasurement>> map = new HashMap<>();
        map.put(profile, measurements);
        profile.setMeasurements(measurements);
        profile.setFractions(TestUtils.generateFractionsTestData(profile));
        profile.setId(1L);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        when(profileService.enrichParsedProfile(map)).thenReturn(profiles);

        ValidationResultDto result = measurementService.validateParsedMeasurements(map);

        assertNotNull(result);
        assertEquals(1, result.getValidProfiles().size());
        assertEquals(0, result.getInvalidProfiles().size());
        verify(profileService).enrichParsedProfile(map);
    }

    @Test
    void validateParsedMeasurements_invalidIncrementationOfMeasurements() {
        Profile profile = TestUtils.generateProfile(null, "profile", "0001");
        List<MeterMeasurement> measurements = TestUtils.generateInvalidMeasurementsTestData();
        Map<Profile, List<MeterMeasurement>> map = new HashMap<>();
        map.put(profile, measurements);
        profile.setMeasurements(measurements);
        profile.setFractions(TestUtils.generateFractionsTestData(profile));
        profile.setId(1L);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        when(profileService.enrichParsedProfile(map)).thenReturn(profiles);

        ValidationResultDto result = measurementService.validateParsedMeasurements(map);

        assertNotNull(result);
        assertEquals(0, result.getValidProfiles().size());
        assertEquals(1, result.getInvalidProfiles().size());
        verify(profileService).enrichParsedProfile(map);
    }

    @Test
    void validateParsedMeasurements_invalidConsumption_LessThenAllowed() {
        Profile profile = TestUtils.generateProfile(null, "profile", "0001");
        List<MeterMeasurement> measurements = TestUtils.generateInvalidConsumedMeasurementsLessThanAllowedConsumptionTestData();
        Map<Profile, List<MeterMeasurement>> map = new HashMap<>();
        map.put(profile, measurements);
        profile.setMeasurements(measurements);
        profile.setFractions(TestUtils.generateFractionsTestData(profile));
        profile.setId(1L);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        when(profileService.enrichParsedProfile(map)).thenReturn(profiles);

        ValidationResultDto result = measurementService.validateParsedMeasurements(map);

        assertNotNull(result);
        assertEquals(0, result.getValidProfiles().size());
        assertEquals(1, result.getInvalidProfiles().size());
        verify(profileService).enrichParsedProfile(map);
    }

    @Test
    void validateParsedMeasurements_invalidConsumption_MoreThenAllowed() {
        Profile profile = TestUtils.generateProfile(null, "profile", "0001");
        List<MeterMeasurement> measurements = TestUtils.generateInvalidConsumedMeasurementsMoreThanAllowedConsumptionTestData();
        Map<Profile, List<MeterMeasurement>> map = new HashMap<>();
        map.put(profile, measurements);
        profile.setMeasurements(measurements);
        profile.setFractions(TestUtils.generateFractionsTestData(profile));
        profile.setId(1L);
        List<Profile> profiles = new ArrayList<>();
        profiles.add(profile);
        when(profileService.enrichParsedProfile(map)).thenReturn(profiles);

        ValidationResultDto result = measurementService.validateParsedMeasurements(map);

        assertNotNull(result);
        assertEquals(0, result.getValidProfiles().size());
        assertEquals(1, result.getInvalidProfiles().size());
        verify(profileService).enrichParsedProfile(map);
    }


    @Test
    void saveAll() {
        List<MeterMeasurement> measurements = TestUtils.generateMeasurementsTestData();

        measurementService.saveAll(measurements);

        verify(measurementRepository).saveAll(measurements);
    }

    @Test
    void getConsumptionForMeterByMonth_countConsumptionForJanuary() {
        Profile profile = new Profile();
        profile.setId(1L);
        when(profileService.getProfileByMeterId("0001")).thenReturn(profile);
        ArgumentCaptor<List<Month>> monthsCaptor = ArgumentCaptor.forClass(List.class);
        when(measurementRepository.findAllByProfileIdAndYearAndMonthIn(anyLong(), anyInt(), monthsCaptor.capture()))
                .thenReturn(List.of(new MeterMeasurement(123, Month.JAN, 2023)));

        Integer result = measurementService.getConsumptionForMeterByMonth("0001", "JAN", 2023);

        assertEquals(123, result);
        verify(profileService).getProfileByMeterId("0001");
        verify(measurementRepository).findAllByProfileIdAndYearAndMonthIn(1L, 2023, monthsCaptor.getValue());
        assertEquals(1, monthsCaptor.getValue().size());
        assertEquals(Month.JAN, monthsCaptor.getValue().get(0));
    }

    @Test
    void getConsumptionForMeterByMonth_countConsumptionForNotFirstMonth() {
        Profile profile = new Profile();
        profile.setId(1L);
        when(profileService.getProfileByMeterId("0001")).thenReturn(profile);
        ArgumentCaptor<List<Month>> monthsCaptor = ArgumentCaptor.forClass(List.class);
        when(measurementRepository.findAllByProfileIdAndYearAndMonthIn(anyLong(), anyInt(), monthsCaptor.capture()))
                .thenReturn(List.of(new MeterMeasurement(123, Month.JUN, 2023),
                        new MeterMeasurement(124, Month.JUL, 2023)));

        Integer result = measurementService.getConsumptionForMeterByMonth("0001", "JUL", 2023);

        assertEquals(1, result);
        verify(profileService).getProfileByMeterId("0001");
        verify(measurementRepository).findAllByProfileIdAndYearAndMonthIn(1L, 2023, monthsCaptor.getValue());
        assertEquals(2, monthsCaptor.getValue().size());
        assertEquals(Month.JUN, monthsCaptor.getValue().get(0));
        assertEquals(Month.JUL, monthsCaptor.getValue().get(1));
    }
}