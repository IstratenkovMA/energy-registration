package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;

import java.util.List;
import java.util.Map;

public interface MeasurementService {
    ValidationResultDto validateParsedMeasurements(Map<Profile, List<MeterMeasurement>> parsedMeasurements);

    void saveAll(List<MeterMeasurement> measurements);

    Integer getConsumptionForMeterByMonth(String meterId, String month, Integer year);
}
