package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.exception.ProfileDataNotFoundInDBException;
import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Business logic for serving profile functionality.
 */
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final FractionRepository fractionRepository;

    /**
     * Transactional method. Save all given profiles to database.
     * @param profiles to be saved.
     */
    @Override
    @Transactional
    public void saveAll(List<Profile> profiles) {
        profileRepository.saveAll(profiles);
    }

    /**
     * Transactional method. Search profile by it's meterId.
     * @param meterId meterId of meter for specific profile in db.
     * @return null or founded profile.
     */
    @Override
    @Transactional
    public Profile getProfileByMeterId(String meterId) {
        return profileRepository.findByMeterId(meterId);
    }

    /**
     * Transactional method.
     * That checks if parsed profile already exists in db. Method sets
     * up it as a reference for measurements. And enriches profile from db with data parsed meterId
     * and parsed measurements.
     * With other words it merge data from db and parsed data from file.
     * @param parsedMeasurements Map profile vs it's measurements.
     * @return list of fulfilled profiles with full data that we have in db and from file.
     */
    @Override
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
                throw new ProfileDataNotFoundInDBException(parsedProfile.getName());
            }
            Profile profileFromDB = namesProfilesFromDB.get(parsedProfile.getName());
            profileFromDB.setMeterId(parsedProfile.getMeterId());
            profileFromDB.setMeasurements(profileMeasurements.getValue());
            List<Fraction> fractionForTheSameYear = fractionRepository.
                    findAllByProfileIdAndYear(profileFromDB.getId(), yearOfMeasurements);
            profileFromDB.setFractions(fractionForTheSameYear);
            enrichedProfiles.add(profileFromDB);
        }
        return enrichedProfiles;
    }
}
