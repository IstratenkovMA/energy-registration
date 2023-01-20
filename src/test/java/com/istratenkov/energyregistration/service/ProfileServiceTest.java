package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.exception.ProfileDataNotFoundInDBException;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
import com.istratenkov.energyregistration.service.impl.ProfileServiceImpl;
import com.istratenkov.energyregistration.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    ProfileRepository profileRepository;
    @Mock
    FractionRepository fractionRepository;
    @InjectMocks
    ProfileServiceImpl profileService;

    @Test
    void saveAll() {
        List<Profile> profiles = List.of(new Profile(), new Profile());

        profileService.saveAll(profiles);

        verify(profileRepository).saveAll(profiles);
    }

    @Test
    void getProfileByMeterId() {
        String meterId = "0001";
        Profile profile = new Profile();
        when(profileRepository.findByMeterId(meterId)).thenReturn(profile);

        Profile result = profileService.getProfileByMeterId(meterId);

        assertEquals(profile, result);
        verify(profileRepository).findByMeterId(meterId);
    }

    @Test
    void enrichProfile_profilesStoredInDb() {
        Profile profile1 = TestUtils.generateProfile(null, "profile", "0001");
        Profile profile2 = TestUtils.generateProfile(null, "profile2", "0002");
        Profile profile3 = TestUtils.generateProfile(null, "profile3", "0003");
        Profile dbProfile1 = TestUtils.generateProfile(1L, "profile", null);
        Profile dbProfile2 = TestUtils.generateProfile(2L, "profile2", null);
        Profile dbProfile3 = TestUtils.generateProfile(3L, "profile3", null);
        Map<Profile, List<MeterMeasurement>> parsedMeasurements = new HashMap<>();
        parsedMeasurements.put(profile1, TestUtils.generateMeasurementsTestData());
        parsedMeasurements.put(profile2, TestUtils.generateMeasurementsTestData());
        parsedMeasurements.put(profile3, TestUtils.generateMeasurementsTestData());
        when(profileRepository.findAllByNameIn(anyList())).thenReturn(List.of(dbProfile1, dbProfile2, dbProfile3));
        when(fractionRepository.findAllByProfileIdAndYear(anyLong(), anyInt()))
                .thenReturn(TestUtils.generateFractionsTestData(profile1));

        List<Profile> result = profileService.enrichProfile(parsedMeasurements);

        assertNotNull(result);
        assertEquals(3, result.size());
        Profile resultProfile = result.get(0);
        assertNotNull(resultProfile.getMeasurements());
        assertNotNull(resultProfile.getFractions());
        assertNotNull(resultProfile.getName());
        assertNotNull(resultProfile.getId());
        assertNotNull(resultProfile.getMeterId());
        verify(profileRepository).findAllByNameIn(anyList());
        verify(fractionRepository, times(3)).findAllByProfileIdAndYear(anyLong(), anyInt());
    }

    @Test
    void enrichProfile_profilesNotStoredInDb() {
        Profile profile = new Profile();
        profile.setName("profile");
        Profile profile1 = new Profile();
        profile1.setName("profile1");
        Profile profile2 = new Profile();
        profile2.setName("profile2");
        Map<Profile, List<MeterMeasurement>> parsedMeasurements = new HashMap<>();
        parsedMeasurements.put(profile, TestUtils.generateMeasurementsTestData());
        parsedMeasurements.put(profile1, TestUtils.generateMeasurementsTestData());
        parsedMeasurements.put(profile2, TestUtils.generateMeasurementsTestData());
        when(profileRepository.findAllByNameIn(anyList())).thenReturn(List.of(profile));

        assertThrowsExactly(ProfileDataNotFoundInDBException.class,
                () -> profileService.enrichProfile(parsedMeasurements));
    }
}