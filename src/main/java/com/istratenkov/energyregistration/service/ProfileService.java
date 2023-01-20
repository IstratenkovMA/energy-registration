package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.dto.EnrichedProfilesDto;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;

import java.util.List;
import java.util.Map;

public interface ProfileService {
    void saveAll(List<Profile> profiles);

    Profile getProfileByMeterId(String meterId);

    EnrichedProfilesDto enrichParsedProfile(Map<Profile, List<MeterMeasurement>> parsedMeasurements);
}
