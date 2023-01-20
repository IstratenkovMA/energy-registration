package com.istratenkov.energyregistration.model.dto;

import com.istratenkov.energyregistration.model.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Used to pass information about profiles after enrichment steps.
 */
@AllArgsConstructor
@Getter
public class EnrichedProfilesDto {
    private List<Profile> enrichedProfiles;
    private List<Profile> profilesFailedToEnrich;
}
