package com.istratenkov.energyregistration.repository;

import com.istratenkov.energyregistration.model.entity.MeterMeasurement;
import com.istratenkov.energyregistration.model.entity.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProfileRepository extends CrudRepository<Profile, Long> {

//    @Query(value = "SELECT p FROM Profile p WHERE p.name IN :names")
//    List<Profile> findAllByNames(@Param("names") Collection<String> names);

    List<Profile> findAllByNameIn(Collection<String> names);
}
