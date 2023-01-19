package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final FractionRepository fractionRepository;

    @Transactional
    public void saveAll(List<Profile> profiles) {
        profileRepository.saveAll(profiles);
    }

    @Transactional
    public List<Profile> enrichProfile(Map<Profile, List<MeterMeasurement>> parsedMeasurements) {
        List<String> profileNamesToEnrich = parsedMeasurements.keySet()
                .stream().map(Profile::getName).collect(Collectors.toList());
        List<Profile> profilesFromDB = profileRepository.findAllByNameIn(profileNamesToEnrich);
        Map<String, Profile> namesProfilesFromDB = profilesFromDB
                .stream().collect(Collectors.toMap(Profile::getName, e -> e));
        List<Profile> enrichedProfiles = new ArrayList<>();
        for (Map.Entry<Profile, List<MeterMeasurement>> profileMeasurements : parsedMeasurements.entrySet()) {
            Integer yearOfMeasurements = profileMeasurements.getValue().get(0).getYear();
            Profile parsedProfile = profileMeasurements.getKey();
            if(!namesProfilesFromDB.containsKey(parsedProfile.getName())) {
                throw new RuntimeException(String.format("Data for %s profile is absent in DB!",
                        parsedProfile.getName()));//todo it's better to create app exception for this
            }
            Profile profileFromDB = namesProfilesFromDB.get(parsedProfile.getName());
            profileFromDB.setMeterId(parsedProfile.getMeterId());
            profileFromDB.setMeasurements(new HashSet<>(profileMeasurements.getValue()));
            List<Fraction> fractionForTheSameYear = fractionRepository.
                    findAllByProfileIdAndYear(profileFromDB.getId(), yearOfMeasurements);
            profileFromDB.setFractions(new HashSet<>(fractionForTheSameYear)); //todo check if we can refactor from set to list
            enrichedProfiles.add(profileFromDB);
        }
        return enrichedProfiles;
    }
}