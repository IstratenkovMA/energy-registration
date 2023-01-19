package com.istratenkov.energyregistration.model.dto;

import com.istratenkov.energyregistration.model.entity.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
public class ValidationResultDto {
    private Set<Profile> invalidProfiles;
    private List<Profile> validProfiles;
}
