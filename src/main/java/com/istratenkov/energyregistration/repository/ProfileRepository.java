package com.istratenkov.energyregistration.repository;

import com.istratenkov.energyregistration.model.entity.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProfileRepository extends CrudRepository<Profile, Long> {

    List<Profile> findAllByNameIn(Collection<String> names);
    Profile findByMeterId(String meterId);
}
