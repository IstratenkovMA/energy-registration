package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FractionService {

    private final FractionRepository fractionRepository;
    private final ProfileRepository profileRepository;

    public ValidationResultDto validateParsedFractions(Map<Profile, List<Fraction>> parsedFractions) {
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
        return new ValidationResultDto(invalidProfiles, validProfiles);
    }

    @Transactional
    public void saveFractions(Map<Profile, List<Fraction>> parsedFractions) {
        List<Fraction> fractions = new ArrayList<>();
        Set<Profile> parsedProfiles = new HashSet<>(parsedFractions.keySet());
        List<Profile> existingProfilesInDB = profileRepository.findAllByNameIn(parsedProfiles
                .stream().map(Profile::getName).collect(Collectors.toList()));
        existingProfilesInDB.forEach(parsedProfiles::remove);
        Map<String, Profile> nameProfileFromDb = existingProfilesInDB.stream()
                .collect(Collectors.toMap(Profile::getName, e -> e));
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
    }
}
