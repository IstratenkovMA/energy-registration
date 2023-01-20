package com.istratenkov.energyregistration.service.impl;

import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
import com.istratenkov.energyregistration.service.FractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Business logic for serving fraction functionality.
 * Validation can be moved from here if need to achieve better single responsibility.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FractionServiceImpl implements FractionService {

    private final FractionRepository fractionRepository;
    private final ProfileRepository profileRepository;

    /**
     * Validation of parsed fractions data from file.
     * Validate sum of 12 month fractions for given profile should be 1.
     *
     * @param parsedFractions map of parsed profile and list of fractions form csv file to be validated.
     * @return ValidationResultDto with failed and succeeded lists of profiles.
     */
    public ValidationResultDto validateParsedFractions(Map<Profile, List<Fraction>> parsedFractions) {
        log.trace("[validateParsedFractions] Profiles: {}", parsedFractions.keySet());
        Set<Profile> invalidProfiles = new HashSet<>();
        List<Profile> validProfiles = new ArrayList<>();
        for (Map.Entry<Profile, List<Fraction>> entry : parsedFractions.entrySet()) {
            Profile profile = entry.getKey();
            List<Fraction> fractions = entry.getValue();
            profile.setFractions(fractions);
            DoubleSummaryStatistics collect = fractions
                    .stream()
                    .map(Fraction::getValue)
                    .collect(Collectors.summarizingDouble(d -> (double) d));
            double sum = collect.getSum();
            if (Math.abs(sum - 1.0) > 0.0001) {
                invalidProfiles.add(profile);
            } else {
                validProfiles.add(profile);
            }
        }
        log.info("[validateParsedFractions] Validation result: valid profiles: ({}) , invalid profiles: ({}).",
                getProfileNamesToString(validProfiles.stream()),
                getProfileNamesToString(invalidProfiles.stream()));
        return new ValidationResultDto(invalidProfiles, validProfiles);
    }

    /**
     * Save information about fractions. Previously try to check
     * if profile for this specific fraction already been presented in db.
     * Decides which profile need to be set as relation for fraction, from db or from parsed file.
     *
     * @param validProfilesForSave map of valid and parsed profile vs list of fractions form csv file to be validated.
     */
    @Transactional
    public void saveFractionsWithProfile(List<Profile> validProfilesForSave) {
        log.info("[saveFractions] Begin saving fractions data for profiles: {}",
                getProfileNamesToString(validProfilesForSave.stream()));
        List<Fraction> fractions = new ArrayList<>();
        Set<Profile> parsedProfiles = new HashSet<>(validProfilesForSave);
        List<Profile> existingProfilesInDB = profileRepository
                .findAllByNameIn(getListNamesFromProfileList(parsedProfiles));
        Map<String, Profile> nameProfileFromDb = transformListToMapNameProfile(existingProfilesInDB);
        for (Profile profile : validProfilesForSave) {
            Profile profileToUpdate = nameProfileFromDb.getOrDefault(profile.getName(), profile);
            List<Fraction> fractionsToSave = profile.getFractions();
            fractionsToSave.forEach(e -> e.setProfile(profileToUpdate));
            fractions.addAll(fractionsToSave);
        }
        profileRepository.saveAll(parsedProfiles);
        fractionRepository.saveAll(fractions);
        log.info("[saveFractions] All data saved for profiles: {}",
                getProfileNamesToString(validProfilesForSave.stream()));
    }

    private List<String> getListNamesFromProfileList(Set<Profile> parsedProfiles) {
        return parsedProfiles.stream().map(Profile::getName).collect(Collectors.toList());
    }

    private Map<String, Profile> transformListToMapNameProfile(List<Profile> existingProfilesInDB) {
        return existingProfilesInDB.stream().collect(Collectors.toMap(Profile::getName, e -> e));
    }

    private String getProfileNamesToString(Stream<Profile> stream) {
        return stream.map(Profile::getName).collect(Collectors.joining(","));
    }
}
