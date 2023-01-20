package com.istratenkov.energyregistration.service.impl;

import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.model.entity.enumeration.Month;
import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
import com.istratenkov.energyregistration.service.FractionService;
import com.istratenkov.energyregistration.service.impl.FractionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FractionServiceTest {

    @Mock
    FractionRepository fractionRepository;
    @Mock
    ProfileRepository profileRepository;
    @InjectMocks
    FractionServiceImpl fractionService;

    @Test
    void validateParsedFractions_SuccessFindValidFractions() {
        Map<Profile, List<Fraction>> testData = generateValidTestData();

        FractionService fractionService = new FractionServiceImpl(fractionRepository, profileRepository);
        ValidationResultDto result = fractionService.validateParsedFractions(testData);

        assertEquals(3, result.getValidProfiles().size());
        assertEquals(0, result.getInvalidProfiles().size());
    }

    @Test
    void validateParsedFractions_SuccessFindInvalidFractions() {
        Map<Profile, List<Fraction>> testData = generateInvalidTestData();

        ValidationResultDto result = fractionService.validateParsedFractions(testData);

        assertEquals(0, result.getValidProfiles().size());
        assertEquals(1, result.getInvalidProfiles().size());
    }

    @Test
    void validateParsedFractions_Null() {
        Assertions.assertThrows(NullPointerException.class, () -> fractionService.validateParsedFractions(null));
    }

    @Test
    void saveFractions_existingProfileInDb() {
        Profile profile = new Profile();
        profile.setName("profile");
        when(profileRepository.findAllByNameIn(anyCollection())).thenReturn(List.of(profile));
        ArgumentCaptor<List<Fraction>> fractionsCaptor = ArgumentCaptor.forClass(List.class);
        when(fractionRepository.saveAll(fractionsCaptor.capture())).thenReturn(List.of(new Fraction()));
        ArgumentCaptor<Set<Profile>> profilesCaptor = ArgumentCaptor.forClass(Set.class);
        when(profileRepository.saveAll(profilesCaptor.capture())).thenReturn(List.of(new Profile()));

        List<Profile> testDataForProfileList = getValidTestDataAsList();
        fractionService.saveFractionsWithProfile(testDataForProfileList);

        verify(profileRepository).findAllByNameIn(anyList());
        verify(profileRepository).saveAll(anySet());
        assertEquals(3, profilesCaptor.getValue().size());
        verify(fractionRepository).saveAll(anyList());
        assertEquals(36, fractionsCaptor.getValue().size());
    }

    @Test
    void saveFractions_notExistingProfileInDb() {
        when(profileRepository.findAllByNameIn(anyCollection())).thenReturn(Collections.emptyList());
        ArgumentCaptor<List<Fraction>> fractionsCaptor = ArgumentCaptor.forClass(List.class);
        when(fractionRepository.saveAll(fractionsCaptor.capture())).thenReturn(List.of(new Fraction()));
        ArgumentCaptor<Set<Profile>> profilesCaptor = ArgumentCaptor.forClass(Set.class);
        when(profileRepository.saveAll(profilesCaptor.capture())).thenReturn(List.of(new Profile()));

        List<Profile> testDataForProfileList = getValidTestDataAsList();
        fractionService.saveFractionsWithProfile(testDataForProfileList);

        verify(profileRepository).findAllByNameIn(anyList());
        verify(profileRepository).saveAll(anySet());
        assertEquals(3, profilesCaptor.getValue().size());
        verify(fractionRepository).saveAll(anyList());
        assertEquals(36, fractionsCaptor.getValue().size());
    }

    private Map<Profile, List<Fraction>> generateValidTestData() {
        Profile profile = new Profile();
        profile.setName("profile");
        List<Fraction> validFractions = new ArrayList<>();
        validFractions.add(new Fraction(0.1f, Month.JAN, 2023, profile));
        validFractions.add(new Fraction(0.1f, Month.FEB, 2023, profile));
        validFractions.add(new Fraction(0.1f, Month.MAR, 2023, profile));
        validFractions.add(new Fraction(0.1f, Month.APR, 2023, profile));
        validFractions.add(new Fraction(0.1f, Month.MAY, 2023, profile));
        validFractions.add(new Fraction(0.1f, Month.JUN, 2023, profile));
        validFractions.add(new Fraction(0.1f, Month.JUL, 2023, profile));
        validFractions.add(new Fraction(0.1f, Month.AUG, 2023, profile));
        validFractions.add(new Fraction(0.05f, Month.SEP, 2023, profile));
        validFractions.add(new Fraction(0.05f, Month.OCT, 2023, profile));
        validFractions.add(new Fraction(0.05f, Month.NOV, 2023, profile));
        validFractions.add(new Fraction(0.05f, Month.DEC, 2023, profile));

        Profile profile1 = new Profile();
        profile1.setName("profile1");
        List<Fraction> validFractions1 = new ArrayList<>();
        validFractions1.add(new Fraction(0.025f, Month.JAN, 2023, profile1));
        validFractions1.add(new Fraction(0.025f, Month.FEB, 2023, profile1));
        validFractions1.add(new Fraction(0.05f, Month.MAR, 2023, profile1));
        validFractions1.add(new Fraction(0.1f, Month.APR, 2023, profile1));
        validFractions1.add(new Fraction(0.1f, Month.MAY, 2023, profile1));
        validFractions1.add(new Fraction(0.2f, Month.JUN, 2023, profile1));
        validFractions1.add(new Fraction(0.1f, Month.JUL, 2023, profile1));
        validFractions1.add(new Fraction(0.2f, Month.AUG, 2023, profile1));
        validFractions1.add(new Fraction(0.05f, Month.SEP, 2023, profile1));
        validFractions1.add(new Fraction(0.05f, Month.OCT, 2023, profile1));
        validFractions1.add(new Fraction(0.05f, Month.NOV, 2023, profile1));
        validFractions1.add(new Fraction(0.05f, Month.DEC, 2023, profile1));

        Profile profile2 = new Profile();
        profile2.setName("profile2");
        List<Fraction> validFractions2 = new ArrayList<>();
        validFractions2.add(new Fraction(0.1f, Month.JAN, 2023, profile2));
        validFractions2.add(new Fraction(0.3f, Month.FEB, 2023, profile2));
        validFractions2.add(new Fraction(0.1f, Month.MAR, 2023, profile2));
        validFractions2.add(new Fraction(0.025f, Month.APR, 2023, profile2));
        validFractions2.add(new Fraction(0.025f, Month.MAY, 2023, profile2));
        validFractions2.add(new Fraction(0.025f, Month.JUN, 2023, profile2));
        validFractions2.add(new Fraction(0.025f, Month.JUL, 2023, profile2));
        validFractions2.add(new Fraction(0.1f, Month.AUG, 2023, profile2));
        validFractions2.add(new Fraction(0.05f, Month.SEP, 2023, profile2));
        validFractions2.add(new Fraction(0.15f, Month.OCT, 2023, profile2));
        validFractions2.add(new Fraction(0.05f, Month.NOV, 2023, profile2));
        validFractions2.add(new Fraction(0.05f, Month.DEC, 2023, profile2));

        Map<Profile, List<Fraction>> testData = new HashMap<>();
        testData.put(profile, validFractions);
        testData.put(profile1, validFractions1);
        testData.put(profile2, validFractions2);
        return testData;
    }

    private List<Profile> getValidTestDataAsList() {
        return generateValidTestData().entrySet()
                .stream()
                .peek(e -> e.getKey().setFractions(e.getValue()))
                .map(e -> e.getKey()).collect(Collectors.toList());
    }

    private Map<Profile, List<Fraction>> generateInvalidTestData() {
        Profile profile = new Profile();
        profile.setName("profile");
        List<Fraction> invalidFractions = new ArrayList<>();
        invalidFractions.add(new Fraction(0.1f, Month.JAN, 2023, profile));
        invalidFractions.add(new Fraction(0.1f, Month.FEB, 2023, profile));
        invalidFractions.add(new Fraction(0.1f, Month.MAR, 2023, profile));
        invalidFractions.add(new Fraction(0.1f, Month.APR, 2023, profile));
        invalidFractions.add(new Fraction(0.1f, Month.MAY, 2023, profile));
        invalidFractions.add(new Fraction(0.1f, Month.JUN, 2023, profile));
        invalidFractions.add(new Fraction(0.1f, Month.JUL, 2023, profile));
        invalidFractions.add(new Fraction(0.1f, Month.AUG, 2023, profile));
        invalidFractions.add(new Fraction(0.1f, Month.SEP, 2023, profile));
        invalidFractions.add(new Fraction(0.1f, Month.OCT, 2023, profile));
        invalidFractions.add(new Fraction(0.1f, Month.NOV, 2023, profile));
        invalidFractions.add(new Fraction(0.1f, Month.DEC, 2023, profile));

        Map<Profile, List<Fraction>> testData = new HashMap<>();
        testData.put(profile, invalidFractions);
        return testData;
    }
}