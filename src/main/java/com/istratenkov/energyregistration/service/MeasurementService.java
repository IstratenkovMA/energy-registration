package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import com.istratenkov.energyregistration.model.entity.enumeration.Month;
import com.istratenkov.energyregistration.model.entity.enumeration.MonthConverter;
import com.istratenkov.energyregistration.repository.MeterMeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeasurementService {
    private final ProfileService profileService;
    private final MeterMeasurementRepository repository;


    public ValidationResultDto validateParsedMeasurements(Map<Profile, List<MeterMeasurement>> parsedMeasurements) {
        List<Profile> profiles = profileService.enrichProfile(parsedMeasurements);
        Set<Profile> invalidProfiles = new HashSet<>();
        for (Profile profile : profiles) {
            Set<MeterMeasurement> measurements = profile.getMeasurements();
            Set<Fraction> fractions = profile.getFractions();
            Map<Month, MeterMeasurement> monthMeasurement = measurements
                    .stream().collect(Collectors.toMap(MeterMeasurement::getMonth, e -> e));
            Map<Month, Fraction> monthFraction = fractions
                    .stream().collect(Collectors.toMap(Fraction::getMonth, e -> e));
            if(!validateIncrementationOfMeasurements(monthMeasurement)
                    || !validateConsumption(monthMeasurement, monthFraction)) {
                invalidProfiles.add(profile);
            }
        }
        profiles.removeAll(invalidProfiles);
        return new ValidationResultDto(invalidProfiles, profiles);
    }

    private boolean validateIncrementationOfMeasurements(Map<Month, MeterMeasurement> monthMeasurement) {
        int previousValue = 0;
        for (int i = 1; i <= 12; i++) {
            MeterMeasurement measurement = monthMeasurement.get(MonthConverter.map.get(i));
            if(previousValue > measurement.getValue()) {
                return false;
            }
            previousValue = measurement.getValue();
        }
        return true;
    }

    private boolean validateConsumption(Map<Month, MeterMeasurement> monthMeasurement, Map<Month, Fraction> monthFraction) {
        long totalConsumed = monthMeasurement.get(Month.DEC).getValue();
        int previousValue = 0;
        for (int i = 1; i <= 12; i++) {
            MeterMeasurement measurement = monthMeasurement.get(MonthConverter.map.get(i));
            Fraction fraction = monthFraction.get(MonthConverter.map.get(i));
            Integer consumedInMonth = measurement.getValue() - previousValue;
            float valueCalculatedByFraction = fraction.getValue() * totalConsumed;
            float tolerance = valueCalculatedByFraction * 0.25f;
            float maximumConsumptionAllowed = valueCalculatedByFraction + tolerance;
            float minimumConsumptionAllowed = valueCalculatedByFraction - tolerance;
            if(consumedInMonth < minimumConsumptionAllowed || consumedInMonth > maximumConsumptionAllowed) {
                return false;
            }
            previousValue = measurement.getValue();
        }
        return true;
    }

    @Transactional
    public void saveAll(List<MeterMeasurement> measurements) {
        repository.saveAll(measurements);
    }
}
