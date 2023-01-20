package com.istratenkov.energyregistration.service.impl;

import com.istratenkov.energyregistration.model.dto.EnrichedProfilesDto;
import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.repository.FractionRepository;
import com.istratenkov.energyregistration.repository.ProfileRepository;
import com.istratenkov.energyregistration.service.ProfileService;
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
     *
     * @param profiles to be saved.
     */
    @Override
    @Transactional
    public void saveAll(List<Profile> profiles) {
        profileRepository.saveAll(profiles);
    }

    /**
     * Transactional method. Search profile by it's meterId.
     *
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
     *
     * @param parsedMeasurements Map profile vs it's measurements.
     * @return list of fulfilled profiles with full data that we have in db and from file.
     */
    @Override
    @Transactional
    public EnrichedProfilesDto enrichParsedProfile(Map<Profile, List<MeterMeasurement>> parsedMeasurements) {
        List<String> profileNamesToEnrich = getListOfProfileNames(parsedMeasurements);
        List<Profile> profilesFromDB = profileRepository.findAllByNameIn(profileNamesToEnrich);
        Map<String, Profile> namesProfilesFromDB = getNameProfileMap(profilesFromDB);

        List<Profile> enrichedProfiles = new ArrayList<>();
        List<Profile> profilesFailedToEnrich = new ArrayList<>();
        for (Map.Entry<Profile, List<MeterMeasurement>> profileMeasurements : parsedMeasurements.entrySet()) {
            boolean profileEnriched = tryToEnrichProfile(namesProfilesFromDB, enrichedProfiles, profileMeasurements);
            if(!profileEnriched) {
                profilesFailedToEnrich.add(profileMeasurements.getKey());
            }
        }
        return new EnrichedProfilesDto(enrichedProfiles, profilesFailedToEnrich);
    }

    private boolean tryToEnrichProfile(Map<String, Profile> namesProfilesFromDB,
                                    List<Profile> enrichedProfiles,
                                    Map.Entry<Profile, List<MeterMeasurement>> profileMeasurements) {
        Integer yearOfMeasurements = profileMeasurements.getValue().get(0).getYear();
        Profile parsedProfile = profileMeasurements.getKey();
        //add to enriched profiles only if it exists in db
        if (isProfilePresentedInDB(namesProfilesFromDB, parsedProfile)) {
            Profile profileFromDB = namesProfilesFromDB.get(parsedProfile.getName());
            profileFromDB.setMeterId(parsedProfile.getMeterId());
            profileFromDB.setMeasurements(profileMeasurements.getValue());
            List<Fraction> fractions = fractionRepository.
                    findAllByProfileIdAndYear(profileFromDB.getId(), yearOfMeasurements);
            if(fractions.isEmpty()) return false; //it means that profile is invalid and validation cannot be performed
            profileFromDB.setFractions(fractions);
            enrichedProfiles.add(profileFromDB);
            return true;
        } else {
            return false;
        }
    }

    private List<String> getListOfProfileNames(Map<Profile, List<MeterMeasurement>> parsedMeasurements) {
        return parsedMeasurements.keySet().stream().map(Profile::getName).collect(Collectors.toList());
    }

    //verify profiles founded in db, is it contains profile from parsed file.
    private boolean isProfilePresentedInDB(Map<String, Profile> namesProfilesFromDB, Profile parsedProfile) {
        return namesProfilesFromDB.containsKey(parsedProfile.getName());
    }

    //to search for element faster than O(n) by profile name
    private Map<String, Profile> getNameProfileMap(List<Profile> profilesFromDB) {
        return profilesFromDB.stream().collect(Collectors.toMap(Profile::getName, e -> e));
    }
}
