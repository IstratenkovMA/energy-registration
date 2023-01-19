package com.istratenkov.energyregistration.repository;

import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.enumeration.Month;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeterMeasurementRepository extends CrudRepository<MeterMeasurement, Long> {
    List<MeterMeasurement> findAllByProfileIdAndYearAndMonthIn(Long id, Integer year, List<Month> months);
    List<MeterMeasurement> findAllByProfileIdAndYear(Long id, Integer year);
}
