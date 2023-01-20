package com.istratenkov.energyregistration.repository;

import com.istratenkov.energyregistration.model.entity.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Spring data repository for given entity Profile.
 * All crud methods and others is generated by spring data.
 */
@Repository
public interface ProfileRepository extends CrudRepository<Profile, Long> {

    /**
     * Search profiles in db by names.
     *
     * @param names is collection of names to use them for profile search.
     * @return all profiles from db that matched or empty collection.
     */
    List<Profile> findAllByNameIn(Collection<String> names);

    /**
     * Search for profile in db by meterId.
     *
     * @param meterId id of meter that is used by specific profile.
     * @return profile from db that matched or null.
     */
    Profile findByMeterId(String meterId);
}
