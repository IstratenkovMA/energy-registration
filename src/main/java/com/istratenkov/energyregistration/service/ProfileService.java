package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface ProfileService {
    void saveAll(List<Profile> profiles);
    Profile getProfileByMeterId(String meterId);
    List<Profile> enrichProfile(Map<Profile, List<MeterMeasurement>> parsedMeasurements);
}
