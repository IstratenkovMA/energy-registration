package com.istratenkov.energyregistration.service;

import com.istratenkov.energyregistration.model.dto.ValidationResultDto;
import com.istratenkov.energyregistration.model.entity.Fraction;
import com.istratenkov.energyregistration.model.entity.Profile;

import java.util.List;
import java.util.Map;

public interface FractionService {
    ValidationResultDto validateParsedFractions(Map<Profile, List<Fraction>> parsedFractions);

    void saveFractionsWithProfile(List<Profile> validProfilesForSave);
}
