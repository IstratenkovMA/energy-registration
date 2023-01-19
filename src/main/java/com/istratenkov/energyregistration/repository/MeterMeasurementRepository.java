package com.istratenkov.energyregistration.repository;

import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeterMeasurementRepository extends CrudRepository<MeterMeasurement, Long> {
}
