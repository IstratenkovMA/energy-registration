package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
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
     * @param parsedFractions map of parsed profile and list of fractions form csv file to be validated.
     * @return ValidationResultDto with failed and succeeded lists of profiles.
     */
    public ValidationResultDto validateParsedFractions(Map<Profile, List<Fraction>> parsedFractions) {
        log.trace("[validateParsedFractions] Profiles: {}", parsedFractions.keySet());
        Set<Profile> invalidProfiles = new HashSet<>();
        List<Profile> validProfiles = new ArrayList<>();
        for (Map.Entry<Profile, List<Fraction>> entry: parsedFractions.entrySet()) {
            Profile profile = entry.getKey();
            List<Fraction> fractions = entry.getValue();
            DoubleSummaryStatistics collect = fractions
                    .stream()
                    .map(Fraction::getValue)
                    .collect(Collectors.summarizingDouble(d -> (double) d));
            double sum = collect.getSum();
            if(Math.abs(sum - 1.0) > 0.0001) {
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
     * @param parsedFractions map of valid and parsed profile vs list of fractions form csv file to be validated.
     */
    @Transactional
    public void saveFractions(Map<Profile, List<Fraction>> parsedFractions) {
        log.info("[saveFractions] Begin saving fractions data for profiles: {}",
                getProfileNamesToString(parsedFractions.keySet().stream()));
        List<Fraction> fractions = new ArrayList<>();
        Set<Profile> parsedProfiles = new HashSet<>(parsedFractions.keySet());
        List<Profile> existingProfilesInDB = profileRepository.findAllByNameIn(parsedProfiles
                .stream().map(Profile::getName).collect(Collectors.toList()));
        existingProfilesInDB.forEach(parsedProfiles::remove);
        Map<String, Profile> nameProfileFromDb = transformListToMapNameProfile(existingProfilesInDB);
        for (Map.Entry<Profile, List<Fraction>> entry : parsedFractions.entrySet()) {
            Profile profile;
            if(!parsedProfiles.contains(entry.getKey())) {
                profile = nameProfileFromDb.get(entry.getKey().getName());
            } else {
                profile = entry.getKey();
            }
            entry.getValue().forEach(e -> e.setProfile(profile));
            fractions.addAll(entry.getValue());
        }
        profileRepository.saveAll(parsedProfiles);
        fractionRepository.saveAll(fractions);
        log.info("[saveFractions] All data saved for profiles: {}",
                getProfileNamesToString(parsedFractions.keySet().stream()));
    }

    private Map<String, Profile> transformListToMapNameProfile(List<Profile> existingProfilesInDB) {
        return existingProfilesInDB.stream().collect(Collectors.toMap(Profile::getName, e -> e));
    }

    private String getProfileNamesToString(Stream<Profile> stream) {
        return stream.map(Profile::getName).collect(Collectors.joining(","));
    }
}
