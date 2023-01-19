package com.istratenkov.energyregistration.repository;

import com.istratenkov.energyregistration.model.entity.Fraction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FractionRepository extends CrudRepository<Fraction, Long> {
    List<Fraction> findAllByProfileIdAndYear(Long profileId, Integer year);
}
