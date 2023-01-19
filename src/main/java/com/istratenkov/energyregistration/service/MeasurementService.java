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

/**
 * Business logic for serving measurements functionality.
 * Validation can be moved from here if need to achieve better single responsibility.
 */
@Service
@RequiredArgsConstructor
public class MeasurementService {
    public static final float TOLERANCE_OF_CONSUMPTION_DEVIATION = 0.25f;
    private final ProfileService profileService;
    private final MeterMeasurementRepository repository;

    /**
     * Validation of parsed measurements data from file.
     * Validate data of measurements that next month data not lower than in previous.
     * Validate consumption of every given month with tolerance of 25% to match previously sent fractions
     * for that profile.
     * @param parsedMeasurements map of parsed profile and list of measurements form csv file to be validated.
     * @return ValidationResultDto with failed and succeeded lists of profiles.
     */
    public ValidationResultDto validateParsedMeasurements(Map<Profile, List<MeterMeasurement>> parsedMeasurements) {
        List<Profile> profiles = profileService.enrichProfile(parsedMeasurements);
        Set<Profile> invalidProfiles = new HashSet<>();
        for (Profile profile : profiles) {
            List<MeterMeasurement> measurements = profile.getMeasurements();
            List<Fraction> fractions = profile.getFractions();
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

    /**
     * Transactional method. Save all measurements to db.
     * @param measurements to be saved.
     */
    @Transactional
    public void saveAll(List<MeterMeasurement> measurements) {
        repository.saveAll(measurements);
    }

    /**
     * Transactional method.
     * Read measurements measurements from database to calculate consumption for specified month.
     * This method functionality can easily be expanded to check range of month, not only one month.
     * @param meterId meterId for which is consumption need to be calculated.
     * @param month name of the month allowed values can be found in
     *             com.istratenkov.energyregistration.model.entity.enumeration.Month
     * @param year year for which data is needed.
     * @return Integer representing consumption for given month.
     */
    @Transactional
    public Integer getConsumptionForMeterByMonth(String meterId, String month, Integer year) {
        Month requiredMonth = Month.valueOf(month);
        Profile profile = profileService.getProfileByMeterId(meterId);
        if(requiredMonth == Month.JAN) {
            return repository.findAllByProfileIdAndYearAndMonthIn(profile.getId(),
                    year, List.of(requiredMonth)).get(0).getValue();
        } else {
            Month previousMonth = MonthConverter.map.get(Month.valueOf(month).getNumber() - 1);
            List<MeterMeasurement> twoMonthMeasurements = repository.findAllByProfileIdAndYearAndMonthIn(
                    profile.getId(),
                    year,
                    List.of(previousMonth, requiredMonth));
            MeterMeasurement first = twoMonthMeasurements.get(0);
            MeterMeasurement second = twoMonthMeasurements.get(1);
            return Math.abs(second.getValue() - first.getValue());
        }
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

    private boolean validateConsumption(Map<Month, MeterMeasurement> monthMeasurement,
                                        Map<Month, Fraction> monthFraction) {
        long totalConsumed = monthMeasurement.get(Month.DEC).getValue();
        int previousValue = 0;
        for (int i = 1; i <= 12; i++) {
            MeterMeasurement measurement = monthMeasurement.get(MonthConverter.map.get(i));
            Fraction fraction = monthFraction.get(MonthConverter.map.get(i));
            Integer consumedInMonth = measurement.getValue() - previousValue;
            float valueCalculatedByFraction = fraction.getValue() * totalConsumed;
            float tolerance = valueCalculatedByFraction * TOLERANCE_OF_CONSUMPTION_DEVIATION;
            float maximumConsumptionAllowed = valueCalculatedByFraction + tolerance;
            float minimumConsumptionAllowed = valueCalculatedByFraction - tolerance;
            if(consumedInMonth < minimumConsumptionAllowed || consumedInMonth > maximumConsumptionAllowed) {
                return false;
            }
            previousValue = measurement.getValue();
        }
        return true;
    }
}
